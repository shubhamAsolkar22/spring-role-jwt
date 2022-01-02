package com.fkog.security.jwt.inputValidator;

public interface Validable<T> {
	void validate(T t);
}
