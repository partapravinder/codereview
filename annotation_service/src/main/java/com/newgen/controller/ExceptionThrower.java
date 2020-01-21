package com.newgen.controller;

import org.springframework.http.HttpStatus;

import com.newgen.exception.CustomException;

public class ExceptionThrower {
	
	public void throwInvalidAnnotationException() throws CustomException{
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Invalid Annotation Id", HttpStatus.BAD_REQUEST);
	}
	
	public void throwDatabaseFailureException() throws CustomException{
		throw new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Database connection failure", HttpStatus.SERVICE_UNAVAILABLE);
	}

	public void throwUnknownErrorException() throws CustomException{
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Unknown Error!", HttpStatus.BAD_REQUEST);
	}
	public void throwInvalidTenantException() throws CustomException{
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Invalid Tenant Id", HttpStatus.BAD_REQUEST);
	}
}
