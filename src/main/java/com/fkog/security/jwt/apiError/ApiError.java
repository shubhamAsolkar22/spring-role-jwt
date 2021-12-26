package com.fkog.security.jwt.apiError;

import org.springframework.http.HttpStatus;

public interface ApiError {
	HttpStatus getStatus();

	String getMessage();

	String getSystemCode();

}
