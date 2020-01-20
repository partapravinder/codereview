package com.newgen.exception;

import org.springframework.http.HttpStatus;

import com.newgen.exception.CustomException;

public class ExceptionThrower {
	public void throwInvalidInputException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Invalid Input", HttpStatus.BAD_REQUEST);
	}

	public void throwInvalidTypeException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "type parameter can contain values folder/content",
				HttpStatus.BAD_REQUEST);
	}

	public void throwDatabaseFailureException() throws CustomException {
		throw new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Database connection failure",
				HttpStatus.SERVICE_UNAVAILABLE);
	}

	public void throwFolderContentExistsException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "There are folders and contents associated",
				HttpStatus.BAD_REQUEST);
	}

	public void throwUnknownErrorException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Unknown Error!", HttpStatus.BAD_REQUEST);
	}
	
	public void throwInvalidTokenException() throws CustomException {
		throw new CustomException(HttpStatus.UNAUTHORIZED.value(), "Unknown Error!", HttpStatus.BAD_REQUEST);
	}

}
