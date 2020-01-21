package com.newgen.controller;

import org.springframework.http.HttpStatus;

import com.newgen.exception.CustomException;

public class ExceptionThrower {

	public void throwInvalidCabinetException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Invalid Cabinet Id", HttpStatus.BAD_REQUEST);
	}
	public void throwLockExistsException() throws CustomException {
		throw new CustomException(HttpStatus.LOCKED.value(), "Lock already exists", HttpStatus.LOCKED);
	}

	public void throwFolderContentExistsException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "There are folders and contents associated",
				HttpStatus.BAD_REQUEST);
	}

	public void throwDatabaseFailureException() throws CustomException {
		throw new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Database connection failure",
				HttpStatus.SERVICE_UNAVAILABLE);
	}

	public void throwFolderNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Folder not found!", HttpStatus.BAD_REQUEST);
	}

	public void throwDataClassNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "DataClass not found!", HttpStatus.BAD_REQUEST);
	}

	public void throwUnknownErrorException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Unknown Error!", HttpStatus.BAD_REQUEST);
	}

	public void throwVersionConflictException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Version conflict!", HttpStatus.BAD_REQUEST);
	}

	public void throwInvalidTenantException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Invalid Tenant Id", HttpStatus.BAD_REQUEST);
	}

	public void throwDataClassCreationException() throws CustomException {
		throw new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error in creating new Cabinet!",
				HttpStatus.SERVICE_UNAVAILABLE);
	}
	public void throwFolderCreationException() throws CustomException {
		throw new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error in creating new Cabinet!",
				HttpStatus.SERVICE_UNAVAILABLE);
	}
}
