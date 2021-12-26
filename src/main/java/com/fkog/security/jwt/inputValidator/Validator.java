package com.fkog.security.jwt.inputValidator;

import java.util.regex.Pattern;

public class Validator {

	
	private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$");
	public static void checkMinLength(String s, int minLength) {
		if( s != null && s.length() < minLength)
			throw new MinLengthException();
	}
	
	public static void checkMaxLength(String s, int maxLength) {
		if( s != null && s.length() > maxLength)
			throw new MaxLengthException();
	}
	
	/**
	 * Username consists of alphanumeric characters (a-zA-Z0-9), lowercase, or uppercase.
	 * Username allowed of the dot (.), underscore (_), and hyphen (-).
	 * The dot (.), underscore (_), or hyphen (-) must not be the first or last character.
	 * The dot (.), underscore (_), or hyphen (-) does not appear consecutively, e.g., java..regex
	 * The number of characters must be between 5 to 20.
	 */
	public static void checkValidUserName(String s) {
		
		if(s!=null && USERNAME_PATTERN.matcher(s).matches()==false) {
			throw new UserNameInvalidException();
		}
	}
}
