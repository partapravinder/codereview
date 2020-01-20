package com.newgen.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception {

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

	public CustomException(int errorCode) {
		super(getMessageForErrorCode(errorCode));
		this.code = errorCode;
		this.message = getMessageForErrorCode(errorCode);
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}

	private static String getMessageForErrorCode(int errorCode) {
		String message = "Unknown Error";
		return message;
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
