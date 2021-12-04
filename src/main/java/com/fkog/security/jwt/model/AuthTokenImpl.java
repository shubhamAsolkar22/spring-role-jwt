package com.fkog.security.jwt.model;

import org.springframework.stereotype.Component;

public class AuthTokenImpl implements AuthToken{


    private String token;
    private String refreshToken;

    public AuthTokenImpl(){

    }

    public AuthTokenImpl(String token, String refreshToken){
        this.token = token;
        this.refreshToken = refreshToken;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
	public String getRefreshToken() {
		return refreshToken;
	}

    @Override
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}


}
