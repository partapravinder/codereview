package com.newgen.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.newgen.dto.StorageCredentialDTO;
import com.newgen.exception.CustomException;
import com.newgen.model.StorageCredentials;
import com.newgen.model.ValidationError;
import com.newgen.service.StorageCredentialService;
import com.newgen.validation.ValidationErrorBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/storagecredentials")
@Api(value = "Storage Credentials", description = "Operations for Storage Credentials")
public class StorageCredentialController {

	private static final Logger logger = LoggerFactory.getLogger(StorageCredentialController.class);

	@Autowired
	StorageCredentialService storageCredentialService;

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create storage credentials", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Storage Credentials", response = StorageCredentials.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<StorageCredentials> createStorageCredentials(
			@Valid @RequestBody StorageCredentialDTO storageCredentialDTO,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException, JSONException {
		logger.debug("Entering createStorageCredentials()");
		
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		StorageCredentials storageCredentials = new StorageCredentials(null, storageCredentialDTO.getName(),
				storageCredentialDTO.getStorageProtocol(), storageCredentialDTO.getAccountName(),
				storageCredentialDTO.getAccountKey(), storageCredentialDTO.getContainerName(), new Date(), null, null,
				tenantId);
		// If container not given, set default
		if (storageCredentials.getContainerName() == null) {
			String guid = UUID.randomUUID().toString();
			storageCredentials.setContainerName(guid);
		}
		// Insert storage credential
		storageCredentials = storageCredentialService.insert(storageCredentials, tenantId);

		logger.debug("Exit createStorageCredentials()");
		return new ResponseEntity<StorageCredentials>(storageCredentials, HttpStatus.CREATED);
	}

	private void throwInvalidTenantException() throws CustomException {
		// TODO Auto-generated method stub
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Invalid Tenant Id", HttpStatus.BAD_REQUEST);

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
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "List all Storage Credentials")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Storage Credentials list", response = StorageCredentials.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<StorageCredentials>> list(@RequestHeader(value = "tenantId") String tenantId)
			throws JSONException, CustomException {
		logger.debug("Entering list()");
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		List<StorageCredentials> credentialList = storageCredentialService.list(tenantId);

		logger.debug("Exit list()");
		return new ResponseEntity<List<StorageCredentials>>(credentialList, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "Read a Storage Credential")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Read Storage Credential", response = StorageCredentials.class),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<StorageCredentials> readStorageCredential(@PathVariable("id") String id,
			@RequestHeader(value = "tenantId") String tenantId) throws JSONException, CustomException {
		logger.debug("Entering readStorageCredential()");
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		StorageCredentials storageCredentials = storageCredentialService.findById(id, tenantId);

		return new ResponseEntity<StorageCredentials>(storageCredentials, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a Storage Credential", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Storage Credential deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> deleteStorageCredential(@PathVariable("id") String id,
			@RequestParam(value = "version", required = false) String version,
			@RequestHeader(value = "tenantId") String tenantId) throws JSONException, CustomException {
		logger.debug("Entering deleteStorageCredential()");
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		storageCredentialService.delete(id, version, tenantId);

		logger.debug("Exit deleteStorageCredential()");
		return new ResponseEntity<String>(id, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update Storage Credentials")
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Storage Credentials updated", response = StorageCredentials.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<StorageCredentials> updateStorageCredential(@RequestBody Map<String, String> updateParams,
			@PathVariable("id") String id, @RequestParam(value = "version", required = false) Long version,
			@RequestHeader(value = "tenantId") String tenantId) throws JSONException, CustomException {
		logger.debug("Entering updateStorageCredential()");
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		StorageCredentials storageCredentials = storageCredentialService.update(id, updateParams, version, tenantId);

		logger.debug("Exit updateStorageCredential()");
		return new ResponseEntity<StorageCredentials>(storageCredentials, HttpStatus.OK);
	}

}
