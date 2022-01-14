package com.fkog.security.jwt.inputValidator;

import java.util.regex.Pattern;

public class Validator {

	private static final Pattern USERNAME_PATTERN = Pattern
			.compile("^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$");

	private static final Pattern EMAIL_PATTERN_OWASP = Pattern
			.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
	private static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^\\+(?:[0-9] ?){6,14}[0-9]$");
	public static void checkMinLength(String s, int minLength) {
		if (s == null || s.length() < minLength)
			throw new MinLengthException();
	}

	public static void checkMaxLength(String s, int maxLength) {
		if (s == null || s.length() > maxLength)
			throw new MaxLengthException();
	}

	/**
	 * Username consists of alphanumeric characters (a-zA-Z0-9), lowercase, or
	 * uppercase. Username allowed of the dot (.), underscore (_), and hyphen (-).
	 * The dot (.), underscore (_), or hyphen (-) must not be the first or last
	 * character. The dot (.), underscore (_), or hyphen (-) does not appear
	 * consecutively, e.g., java..regex The number of characters must be between 5
	 * to 20.
	 */
	public static void checkValidUserName(String s) {
		if (s == null || USERNAME_PATTERN.matcher(s).matches() == false) {
			throw new UserNameInvalidException();
		}
	}

	public static void checkValidPassword(String s) {
		if (s == null || s.length() < 8 || s.length() > 12) {
			throw new WeakPasswordException();
		}
	}
	
	public static void checkValidEmail(String s) {
		if (s == null || EMAIL_PATTERN_OWASP.matcher(s).matches()==false) {
			throw new MalformedEmailString();
		}
	}
	
	public static void checkValidPhoneNumber(String s) {
		if (s == null || MOBILE_NUMBER_PATTERN.matcher(s).matches()==false) {
			throw new IndianMobileNumberInvalidException();
		}
	}
	
	public static void checkNull(Object s) {
		if(s==null)
			throw new DisallowedNullValueException();
	}
}
