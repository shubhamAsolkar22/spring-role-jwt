package com.fkog.security.jwt.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fkog.security.jwt.config.TokenProvider;
import com.fkog.security.jwt.dao.UserDao;
import com.fkog.security.jwt.inputValidator.UserDtoValidator;
import com.fkog.security.jwt.inputValidator.Validable;
import com.fkog.security.jwt.inputValidator.Validator;
import com.fkog.security.jwt.model.AuthToken;
import com.fkog.security.jwt.model.LogoutUserDto;
import com.fkog.security.jwt.model.Role;
import com.fkog.security.jwt.model.User;
import com.fkog.security.jwt.model.UserDto;
import com.fkog.security.jwt.service.RoleService;
import com.fkog.security.jwt.service.UserService;


@Service  (value = "userService") 
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
    	UserDtoValidator.getInstance().validate(user);
    	
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
    
    @Override
    public void logoutUser(LogoutUserDto logoutUserDto) {
    	userDao.updateLastLoggedOut(new Date(),logoutUserDto.getUsername());
    }

}
