package com.newgen.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.newgen.exception.CustomException;
import com.newgen.model.ExceptionResponse;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(CustomException.class)
	public @ResponseBody ExceptionResponse handleWSException(CustomException ex, HttpServletRequest request) {
		int statusCode = 0;
		String statusMessage = "Failure";

		return ExceptionResponse.generateErrorResponse(statusCode, statusMessage, ex.getCode(), ex.getMessage());
	}

}