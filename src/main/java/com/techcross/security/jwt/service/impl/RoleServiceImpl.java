package com.techcross.security.jwt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techcross.security.jwt.dao.RoleDao;
import com.techcross.security.jwt.model.Role;
import com.techcross.security.jwt.service.RoleService;

@Service(value = "roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public Role findByName(String name) {
        Role role = roleDao.findRoleByName(name);
        return role;
    }
}
