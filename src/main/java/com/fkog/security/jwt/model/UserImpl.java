package com.fkog.security.jwt.model;

import org.springframework.stereotype.Component;

import com.fkog.security.jwt.entity.User;

@Component
public class UserImpl implements UserDto,LoginUser {

    
    private String username;
    private String password;
    private String email;
    private String phone;
    private String name;
    private String businessTitle;


    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getEmail() {
        return email;
    }
    @Override
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String getPhone() {
        return phone;
    }
    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String getBusinessTitle() {
        return businessTitle;
    }
    @Override
    public void setBusinessTitle(String businessTitle) {
        this.businessTitle = businessTitle;
    }

    public User getUserFromDto(){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setName(name);
        user.setBusinessTitle(businessTitle);
        
        return user;
    }
    

}
