package com.newgen.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.newgen.exception.CustomException;
//import com.newgen.model.LogEntity;
import com.newgen.model.LogEntity;
import com.newgen.repository.LoggingRepository;
import com.newgen.request.LogRequest;
import com.newgen.service.LoggingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/logging")
@Api(value = "Logging", description = "Operations for Metric")
public class LoggingController extends ExceptionThrower {

	@Autowired
	LoggingService loggingService;

	/*
	 * @Autowired LoggingDaoImpl loggingDaoImpl;
	 */

	@Autowired
	LoggingRepository loggingRepository;

	@RequestMapping(value = "/saveLog", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Save Log", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Log Created", response = LogEntity.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public String saveLogs(@RequestHeader(value = "tenantId") String tenantId, @RequestBody LogRequest logRequest)
			throws CustomException, InterruptedException, JSONException, IOException {
		return loggingService.saveLogs(tenantId, logRequest);

	}

}
