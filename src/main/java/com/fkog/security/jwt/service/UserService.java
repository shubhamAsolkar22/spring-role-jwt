package com.fkog.security.jwt.service;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.fkog.security.jwt.entity.User;
import com.fkog.security.jwt.model.AuthToken;
import com.fkog.security.jwt.model.UserDto;

public interface UserService {
    User save(UserDto user);
    List<User> findAll();
    User findOne(String username);
    AuthToken refreshToken(String refreshToken);
}
