package com.fkog.security.jwt.inputValidator;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestValidator {

	@Test
	public void testMobileNumber() {
		
		Assertions.assertThrows(IndianMobileNumberInvalidException.class, () -> {
	        Validator.checkValidPhoneNumber(null);
	    });
		
		Assertions.assertThrows(IndianMobileNumberInvalidException.class, () -> {
	        Validator.checkValidPhoneNumber("9198789456 2");
	    });
		
		Assertions.assertDoesNotThrow( () -> {
	        Validator.checkValidPhoneNumber("+91 8779613216");
	    });
		
	}
}
