package com.fkog.security.jwt.inputValidator;

abstract class AbstractValidable<T> implements Validable<T> {

	public void validate(T k) {
		if(k==null)
			throw new InputValidatorException();
		customChecks(k);
	}
	
	protected abstract void customChecks(T k);
}
