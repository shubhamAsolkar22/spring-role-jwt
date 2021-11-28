package com.techcross.security.jwt.controller;

import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techcross.security.jwt.config.TokenProvider;
import com.techcross.security.jwt.model.AuthToken;
import com.techcross.security.jwt.model.LoginUser;
import com.techcross.security.jwt.model.User;
import com.techcross.security.jwt.model.UserDto;
import com.techcross.security.jwt.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/users")
public class UserController {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Resource(name = "userService")
	private UserDetailsService userDetailsService;

	@Autowired
	private TokenProvider jwtTokenUtil;

	@Value("${jwt.header.string}")
	public String HEADER_STRING;

	@Value("${jwt.token.prefix}")
	public String TOKEN_PREFIX;

	@PostMapping(value = "/token")
	public ResponseEntity<?> generateToken(@RequestBody LoginUser loginUser) throws AuthenticationException {

		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		final String token = jwtTokenUtil.generateToken(authentication);
		final String refreshToken = jwtTokenUtil.generateRefreshToken(authentication);
		return ResponseEntity.ok(new AuthToken(token, refreshToken));
	}

	@PostMapping(path = "/new")
	public User saveUser(@RequestBody UserDto user) {
		return userService.save(user);
	}

	@GetMapping(value = "/refreshed-token")
	public ResponseEntity<?> generateRefreshToken(HttpServletRequest req) throws AuthenticationException {
		final String authHeader = req.getHeader(HEADER_STRING);
		final String refreshToken = authHeader.replace(TOKEN_PREFIX, "");

		final AuthToken aToken = userService.refreshToken(refreshToken);

		return ResponseEntity.ok(aToken);
	}

}
