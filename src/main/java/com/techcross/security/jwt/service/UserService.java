package com.techcross.security.jwt.service;

import java.util.List;

import com.techcross.security.jwt.model.AuthToken;
import com.techcross.security.jwt.model.User;
import com.techcross.security.jwt.model.UserDto;

public interface UserService {
    User save(UserDto user);
    List<User> findAll();
    User findOne(String username);
    AuthToken refresh(String refreshToken);
}
