package com.fkog.security.jwt.model;

import com.fkog.security.jwt.entity.User;

public interface UserDto {

	String getUsername();

	void setUsername(String username);

	String getPassword();

	void setPassword(String password);

	String getEmail();

	void setEmail(String email);

	String getPhone();

	void setPhone(String phone);

	String getName();

	void setName(String name);

	String getBusinessTitle();

	void setBusinessTitle(String businessTitle);

	User getUserFromDto();

}