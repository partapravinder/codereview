package com.newgen.model;

import org.springframework.stereotype.Component;

@Component
public class ExceptionResponse {

	private int code;
	private String message;
	private int subCode;
	private String subMessage;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getSubCode() {
		return subCode;
	}

	public void setSubCode(int subCode) {
		this.subCode = subCode;
	}

	public String getSubMessage() {
		return subMessage;
	}

	public void setSubMessage(String subMessage) {
		this.subMessage = subMessage;
	}

	public static ExceptionResponse generateErrorResponse(Integer statusCode, String statusMessage, Integer errorCode,
			String errorMessage) {
		ExceptionResponse response = new ExceptionResponse();
		response.setCode(statusCode);
		response.setMessage(statusMessage);
		response.setSubCode(errorCode);
		response.setSubMessage(errorMessage);
		return response;
	}
}
