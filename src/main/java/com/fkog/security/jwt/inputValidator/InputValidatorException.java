package com.fkog.security.jwt.inputValidator;

class InputValidatorException extends RuntimeException {
	public final int responseStatus = 422;

}
