package com.newgen.exception;

import groovy.transform.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
class ApiValidationError {
	private String object;
	private String field;
	private Object rejectedValue;
	private String message;

	ApiValidationError(String object, String message) {
		this.object = object;
		this.message = message;
	}
}