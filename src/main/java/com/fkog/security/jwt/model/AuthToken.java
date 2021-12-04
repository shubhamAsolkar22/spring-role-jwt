package com.fkog.security.jwt.model;

public interface AuthToken {

	String getToken();

	void setToken(String token);

	String getRefreshToken();

	void setRefreshToken(String refreshToken);

}