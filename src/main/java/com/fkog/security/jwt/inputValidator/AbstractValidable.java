package com.fkog.security.jwt.inputValidator;

abstract class AbstractValidable<T> implements Validable<T> {

	public void validate(T k) {
		customChecks(k);
	}
	
	protected abstract void customChecks(T k);
}
