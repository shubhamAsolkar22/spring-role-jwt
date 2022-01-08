package com.fkog.security.jwt.config;

import io.jsonwebtoken.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fkog.security.jwt.dao.UserDao;
import com.fkog.security.jwt.model.User;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements Serializable {

	@Value("${jwt.token.validity}")
	public long TOKEN_VALIDITY;

	@Value("${jwt.signing.key}")
	public String SIGNING_KEY;

	@Value("${jwt.authorities.key}")
	public String AUTHORITIES_KEY;

	@Value("${jwt.logout.enabled}")
	public String LOGOUT_ENABLED;

	@Autowired
	private UserDao userDao;

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getIssuedAt(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(Authentication authentication) {
		return getConfiguredToken(authentication, TOKEN_VALIDITY);
	}

	private String getConfiguredToken(Authentication authentication, long tokenValidity) {
		String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		return Jwts.builder().setSubject(authentication.getName()).claim(AUTHORITIES_KEY, authorities)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + tokenValidity * 1000))
				.signWith(SignatureAlgorithm.HS256, SIGNING_KEY).compact();
	}

	public String generateRefreshToken(Authentication authentication) {
		return getConfiguredToken(authentication, TOKEN_VALIDITY * 2);
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isLoggedOutToken(token));
	}

	private boolean isLoggedOutToken(String token) {
		if (Boolean.valueOf(LOGOUT_ENABLED)) {
			final String username = getUsernameFromToken(token);
			final Date issuedAt = getIssuedAt(token);
			final User currentUser = userDao.findByUsername(username);
			if (currentUser != null && currentUser.getLastLoggedOut() != null && issuedAt != null
					&& issuedAt.before(currentUser.getLastLoggedOut()))
				return true;
			else
				return false;
		}
		return false;
	}

	public UsernamePasswordAuthenticationToken getAuthenticationToken(final String token,
			final Authentication existingAuth, final UserDetails userDetails) {

		final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);

		final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

		final Claims claims = claimsJws.getBody();

		final Collection<? extends GrantedAuthority> authorities = Arrays
				.stream(claims.get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
	}

}
