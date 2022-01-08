package com.fkog.security.jwt.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fkog.security.jwt.model.User;

@Repository
public interface UserDao extends CrudRepository<User, Long> {
    User findByUsername(String username);
    
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update User u set u.lastLoggedOut = ?1 where u.username = ?2")
    int updateLastLoggedOut(Date lastLoggedOut,String username);
    
}