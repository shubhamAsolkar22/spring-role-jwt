package com.fkog.security.jwt.apiError;

import org.springframework.http.HttpStatus;

public class ApiErrorImpl implements ApiError {

	private final HttpStatus status;
	private final String message;
	private final String systemCode;
	
	public ApiErrorImpl(HttpStatus status,String message, String systemCode) {
		this.status = status;
		this.message = message;
		this.systemCode = systemCode;
	}

	@Override
	public HttpStatus getStatus() {
		return this.status;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public String getSystemCode() {
		return this.systemCode;
	}
	
	
}
