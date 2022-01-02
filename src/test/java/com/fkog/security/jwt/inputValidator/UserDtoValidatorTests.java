package com.fkog.security.jwt.inputValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fkog.security.jwt.model.UserDto;

public class UserDtoValidatorTests {

	@Test
	public void checkNullValidation() {
		Assertions.assertThrows(InputValidatorException.class, () -> {
	        UserDtoValidator.getInstance().validate(null);
	    });
	}
	
	@Test
	public void checkWeakPassword() {
		UserDto u = new UserDto();
		u.setBusinessTitle("dev");
		u.setEmail("a@e.com");
		u.setName("a");
		u.setPassword("r");
		u.setPhone("+91 8779828648");
		Assertions.assertThrows(WeakPasswordException.class, () -> {
	        UserDtoValidator.getInstance().validate(u);
	    });
	}
}
