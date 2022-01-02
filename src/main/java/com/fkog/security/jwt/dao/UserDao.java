package com.fkog.security.jwt.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fkog.security.jwt.model.User;

@Repository
public interface UserDao extends CrudRepository<User, Long> {
    User findByUsername(String username);
    
    @Modifying
    @Query("update User u set u.lastLoggedOut = ?1 where u.id = ?2")
    void updateLastLoggedOut(Date lastLoggedOut,long id);
    
}