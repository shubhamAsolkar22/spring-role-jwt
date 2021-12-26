package com.fkog.security.jwt.inputValidator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fkog.security.jwt.apiError.ApiError;
import com.fkog.security.jwt.apiError.ApiErrorImpl;

@ControllerAdvice
public class AppControllerAdvice {

	@ExceptionHandler(UserNameInvalidException.class)
	public ResponseEntity<?> handleUserNameInvalidException(UserNameInvalidException ex){
		ApiError error = new ApiErrorImpl(HttpStatus.UNPROCESSABLE_ENTITY, "Username is not acceptable.Username consists of alphanumeric characters (a-zA-Z0-9), lowercase, or uppercase.\n"
				+ "Username allowed of the dot (.), underscore (_), and hyphen (-).\n"
				+ "The dot (.), underscore (_), or hyphen (-) must not be the first or last character.\n"
				+ "The dot (.), underscore (_), or hyphen (-) does not appear consecutively, e.g., java..regex\n"
				+ "The number of characters must be between 5 to 20.", "422");
		return new ResponseEntity<ApiError>(error,error.getStatus());
	}
}
