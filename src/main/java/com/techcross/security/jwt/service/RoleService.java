package com.techcross.security.jwt.service;

import com.techcross.security.jwt.model.Role;

public interface RoleService {
    Role findByName(String name);
}
