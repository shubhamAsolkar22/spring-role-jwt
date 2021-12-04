package com.fkog.security.jwt.service;

import com.fkog.security.jwt.entity.Role;

public interface RoleService {
    Role findByName(String name);
}
