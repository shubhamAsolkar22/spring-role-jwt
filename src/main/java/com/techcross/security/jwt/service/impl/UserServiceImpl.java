package com.techcross.security.jwt.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import com.techcross.security.jwt.config.TokenProvider;
import com.techcross.security.jwt.dao.UserDao;
import com.techcross.security.jwt.model.AuthToken;
import com.techcross.security.jwt.model.Role;
import com.techcross.security.jwt.model.User;
import com.techcross.security.jwt.model.UserDto;
import com.techcross.security.jwt.service.RoleService;
import com.techcross.security.jwt.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	@Value("${jwt.token.prefix}")
	public String TOKEN_PREFIX;

	@Autowired
	private TokenProvider jwtTokenUtil;

	@Value("${jwt.header.string}")
	public String HEADER_STRING;

	@Resource(name = "userService")
	private UserDetailsService userDetailsService;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				getAuthority(user));
	}

	private Set<SimpleGrantedAuthority> getAuthority(User user) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
		});
		return authorities;
	}

	public List<User> findAll() {
		List<User> list = new ArrayList<>();
		userDao.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public User findOne(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public User save(UserDto user) {

		User nUser = user.getUserFromDto();
		nUser.setPassword(bcryptEncoder.encode(user.getPassword()));

		Role role = roleService.findByName("USER");
		Set<Role> roleSet = new HashSet<>();
		roleSet.add(role);

		if (nUser.getEmail().split("@")[1].equals("admin.edu")) {
			role = roleService.findByName("ADMIN");
			roleSet.add(role);
		}

		nUser.setRoles(roleSet);
		return userDao.save(nUser);
	}

	@Override
	public AuthToken refreshToken(String refreshToken) {
		AuthToken authToken = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		final String newToken = jwtTokenUtil.generateToken(authentication);

		authToken = new AuthToken(newToken, refreshToken);

		return authToken;
	}

}
