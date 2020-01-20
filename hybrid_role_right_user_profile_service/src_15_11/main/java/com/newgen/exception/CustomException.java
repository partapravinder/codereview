package com.newgen.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception {//NOSONAR

	private static final long serialVersionUID = 1L;
	private final int code;
	private final String message;
	private final HttpStatus httpStatus;

	public CustomException(int code, String message, HttpStatus httpStatus) {
		super();
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
