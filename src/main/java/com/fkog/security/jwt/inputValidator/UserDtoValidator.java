package com.fkog.security.jwt.inputValidator;

import com.fkog.security.jwt.model.UserDto;

public class UserDtoValidator extends AbstractValidable<UserDto>{

	private static final UserDtoValidator INSTANCE = new UserDtoValidator();
	
	private UserDtoValidator() {
		
	}
	
	public static Validable<UserDto> getInstance() {
		return INSTANCE;
	}
	
	@Override
	protected void customChecks(UserDto userDto) {
		Validator.checkNull(userDto);
		Validator.checkValidEmail(userDto.getEmail());
		Validator.checkValidPassword(userDto.getPassword());
		Validator.checkValidPhoneNumber(userDto.getPhone());
		Validator.checkValidUserName(userDto.getUsername());
	}
}
