package com.fkog.security.jwt.service;

import java.util.List;

import com.fkog.security.jwt.model.AuthToken;
import com.fkog.security.jwt.model.User;
import com.fkog.security.jwt.model.UserDto;
import org.springframework.stereotype.Service;

public interface UserService {
    User save(UserDto user);
    List<User> findAll();
    User findOne(String username);
    AuthToken refreshToken(String refreshToken);
}
