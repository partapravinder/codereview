package com.newgen.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.newgen.exception.CustomException;
import com.newgen.model.ExceptionResponse;

@ControllerAdvice
public class ErrorHandlingController {
	
	private final Logger logger = LoggerFactory.getLogger(ErrorHandlingController.class);
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> generalException(Exception e){
		ExceptionResponse eR = new ExceptionResponse();
		eR.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		eR.setMessage(e.getMessage());
		logger.debug(e.getMessage());
		e.printStackTrace();
		return new ResponseEntity<>(eR,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ExceptionResponse> invalidInputException(CustomException e){
		ExceptionResponse eR = new ExceptionResponse();
		eR.setCode(e.getCode());
		eR.setMessage(e.getMessage());
		logger.debug(e.getMessage());
		e.printStackTrace();
		return new ResponseEntity<>(eR,e.getHttpStatus());
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public ResponseEntity<ExceptionResponse> duplicateKeyException(DuplicateKeyException e){
		ExceptionResponse eR = new ExceptionResponse();
		eR.setCode(HttpStatus.LOCKED.value());
		eR.setMessage("Lock already exists");
		logger.debug(e.getMessage());
		e.printStackTrace();
		return new ResponseEntity<>(eR,HttpStatus.LOCKED);
	}
	
	@ExceptionHandler(DataAccessResourceFailureException.class)
	public ResponseEntity<ExceptionResponse> dataAccessException(DataAccessResourceFailureException e){
		ExceptionResponse eR = new ExceptionResponse();
		eR.setCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		eR.setMessage("Database connection failure");
		logger.debug(e.getMessage());
		e.printStackTrace();
		return new ResponseEntity<>(eR,HttpStatus.SERVICE_UNAVAILABLE);
	}
}
