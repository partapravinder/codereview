package com.newgen.controller;

import org.springframework.http.HttpStatus;

import com.newgen.exception.CustomException;

public class ExceptionThrower {

	public void throwDatabaseFailureException(Exception ex) throws CustomException {
		throw new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(),"Database connection failure",HttpStatus.SERVICE_UNAVAILABLE);
	}

	public void throwStorageCredentialNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(),"Storage Credential not found!",HttpStatus.BAD_REQUEST);
	}
	public void throwInvalidTenantException() throws CustomException{
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Invalid Tenant Id", HttpStatus.BAD_REQUEST);
	}
	public void throwStorageLocationNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(),"Storage Location not found!",HttpStatus.BAD_REQUEST);
	}

	public void throwStorageProcessNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(),"Storage Process not found!",HttpStatus.BAD_REQUEST);
	}

	public void throwVersionConflictException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(),"Version conflict!",HttpStatus.BAD_REQUEST);
	}

	public void throwUnknownErrorException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(),"Unknown Error!",HttpStatus.BAD_REQUEST);
	}
}
