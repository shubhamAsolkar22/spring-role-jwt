package com.fkog.security.jwt.service;

import com.fkog.security.jwt.model.Role;

public interface RoleService {
    Role findByName(String name);
}
