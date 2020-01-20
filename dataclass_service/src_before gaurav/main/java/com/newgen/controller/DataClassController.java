package com.newgen.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.dto.DataClassBulkDTO;
import com.newgen.dto.DataClassDTO;
import com.newgen.exception.CustomException;
import com.newgen.model.DataClass;
import com.newgen.model.ValidationError;
import com.newgen.service.DataClassService;
import com.newgen.validation.ValidationErrorBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/dataclass")
@Api(value = "Dataclass", description = "Operations for Dataclasses")
public class DataClassController extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(DataClassController.class);

	@Autowired
	DataClassService dataClassService;

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create a Dataclass", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Dataclass created", response = DataClass.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<DataClass> createDataclass(@Valid @RequestBody DataClassDTO dataClassDTO,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException {
		logger.debug("Entering createDataclass()");
		DataClass dataClass = null;
		Date date = new Date();
		dataClass = new DataClass(dataClassDTO.getId(), dataClassDTO.getDataClassName(),
				dataClassDTO.getDataDefComment(), dataClassDTO.getACL(), dataClassDTO.getEnableLogFlag(),
				dataClassDTO.getACLMoreFlag(), dataClassDTO.getType(), null, dataClassDTO.getGroupId(),
				dataClassDTO.getUnused(), dataClassDTO.getFDFlag(), dataClassDTO.getAccessType(),
				dataClassDTO.getDataFields());
		dataClass.setTenantId(tenantId);
		dataClass.setAccessDateTime(date);
		dataClass.setRevisedDateTime(date);

		if (dataClass.getDataFields() != null) {
			dataClass.setFieldCount(dataClass.getDataFields().size());
		} else {
			dataClass.setFieldCount(0);
		}

		dataClass = dataClassService.insert(dataClass);
		logger.debug("Exit createDataclass()");
		if (dataClass == null)
			throwDataClassCreationException();
		return new ResponseEntity<DataClass>(dataClass, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/bulk", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create a Dataclass", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Dataclass created", response = DataClass.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<DataClass>> createDataclassBulk(@Valid @RequestBody DataClassBulkDTO dataClassBulkDTO,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException {
		logger.debug("Entering createDataclass()");
		List<DataClass> dataClasses = null;
		dataClasses = dataClassBulkDTO.getDataClasses();
		List<DataClass> createdDataClasses = dataClassService.bulkInsert(dataClasses, tenantId);
		logger.debug("Exit createDataclass()");
		if (dataClasses == null)
			throwDataClassCreationException();
		return new ResponseEntity<List<DataClass>>(createdDataClasses, HttpStatus.CREATED);
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ValidationError handleException(MethodArgumentNotValidException exception) {
		return createValidationError(exception);
	}

	private ValidationError createValidationError(MethodArgumentNotValidException exception) {
		return ValidationErrorBuilder.fromBindingErrors(exception.getBindingResult());
	}

	@RequestMapping(method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Searching for Dataclasses", notes = "Other fields may be included to further filter the list returned.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Dataclass list", response = DataClass.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<DataClass>> searchDataclasses(HttpServletRequest request)
			throws CustomException, JSONException {
		logger.debug("Entering searchDataclasses()");
		if (request.getHeader("tenantId") == null) {
			throwInvalidTenantException();
		}
		String tenantId = request.getHeader("tenantId").toString();
		List<DataClass> dataClassList = dataClassService.search(request.getParameterMap(), tenantId);
		logger.debug("Exit searchDataclasses()");
		if (dataClassList.isEmpty())
			return new ResponseEntity<List<DataClass>>(dataClassList, HttpStatus.NOT_FOUND);
		return new ResponseEntity<List<DataClass>>(dataClassList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/dataClassName/{dataClassName}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Dataclasses by Page No.", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get Content Status", response = DataClass.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<DataClass> getDataClassByName(HttpServletRequest request,
			@PathVariable(value = "dataClassName", required = true) String dataClassName) throws CustomException {
		logger.debug("Entering searchDataclassesByPage()");
		System.out.println(dataClassName);
		if (request.getHeader("tenantId") == null) {
			throwInvalidTenantException();
		}
		String tenantId = request.getHeader("tenantId").toString();
		DataClass dataClass = dataClassService.findByName(dataClassName, tenantId);
		logger.debug("Exit searchDataclassesByPage()");
		if (dataClass == null) {
			return new ResponseEntity<DataClass>(dataClass, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<DataClass>(dataClass, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Dataclasses by Page No.", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get Content Status", response = DataClass.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<DataClass> getDataClassById(HttpServletRequest request,
			@PathVariable(value = "id", required = true) String id) throws CustomException {
		logger.debug("Entering searchDataclassesByPage()");

		String tenantId = request.getHeader("tenantId").toString();
		DataClass dataClass = dataClassService.findById(id, tenantId);
		logger.debug("Exit searchDataclassesByPage()");
		if (dataClass == null) {
			return new ResponseEntity<DataClass>(dataClass, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<DataClass>(dataClass, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a Dataclass", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Dataclass deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<DataClass> deleteDataClass(@PathVariable("id") String id,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException, JSONException {
		logger.debug("Entering deleteDataclass()");
		if (tenantId == null) {
			throwInvalidTenantException();
		}
		DataClass dataClass = dataClassService.deleteDataclass(id, tenantId);
		if (dataClass == null) {
			return new ResponseEntity<DataClass>(dataClass, HttpStatus.NOT_FOUND);
		}
		logger.debug("Exit deleteDataclass()");
		return new ResponseEntity<DataClass>(dataClass, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/removeField/{id}/{fieldName}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a Dataclass", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Dataclass deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<DataClass> removeDataClassField(@PathVariable("id") String id,
			@PathVariable("fieldName") String fieldName, @RequestHeader(value = "tenantId") String tenantId)
			throws CustomException, JSONException, JsonParseException, JsonMappingException, IOException {
		logger.debug("Entering removeDataClassField()");
		if (tenantId == null) {
			throwInvalidTenantException();
		}
		DataClass dataClass = dataClassService.deleteDataClassField(id, fieldName, tenantId);

		if (dataClass == null)
			throwUnknownErrorException();

		logger.debug("Exit removeDataClassField()");
		return new ResponseEntity<DataClass>(dataClass, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update Dataclass Info")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Dataclass updated", response = DataClass.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<DataClass> updateDataclass(@RequestBody String updateDataClassParams,
			@PathVariable("id") String id, @RequestHeader(value = "tenantId") String tenantId)
			throws CustomException, IOException {
		logger.debug("Entering updateDataclass()");
		DataClass dataClass = null;
		try {
			dataClass = dataClassService.update(id, updateDataClassParams, tenantId);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		}
		logger.debug("Exit updateDataclass()");
		if (dataClass == null)
			throwUnknownErrorException();
		return new ResponseEntity<DataClass>(dataClass, HttpStatus.OK);
	}

}
