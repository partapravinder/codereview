package com.newgen.wrapper.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.newgen.controller.StorageCredentialController;
import com.newgen.dto.StorageCredentialDTO;
import com.newgen.error.handler.RestResponseErrorHandler;
import com.newgen.model.StorageCredentials;
import com.newgen.model.ValidationError;
import com.newgen.validation.ValidationErrorBuilder;
import com.newgen.wrapper.service.WrapperService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Component
@Profile({ "production", "default" })
public class WrapperServiceImpl implements WrapperService {

	private static final Logger logger = LoggerFactory.getLogger(StorageCredentialController.class);

	@Autowired
	private RestTemplate restTemplate;

//	@Autowired
//	private EurekaUrlResolver eurekaUrlResolver;
//	
//	@Value("${service.storage.serviceId}")
//	private String storageServiceId;
	
	@Value("${storage.service.url}")
	private String storageurl;
	
	private String storageCredentialPath= "/storagecredentials";

	public ResponseEntity<String> createStorageCredentials(
			StorageCredentialDTO storageCredentialDTO,String tenantId) {
		logger.debug("Entering createStorageCredentials()");
		//String storageurl = eurekaUrlResolver.procureUrl(storageServiceId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("tenantId", tenantId);
		HttpEntity<StorageCredentialDTO> request = new HttpEntity<>(storageCredentialDTO, headers);
		ResponseEntity<String> response = restTemplate.exchange(storageurl+storageCredentialPath, HttpMethod.POST, request, String.class);
		logger.debug("Exit createStorageCredentials()");
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ValidationError handleException(MethodArgumentNotValidException exception) {
		return createValidationError(exception);
	}

	private ValidationError createValidationError(MethodArgumentNotValidException exception) {
		return ValidationErrorBuilder.fromBindingErrors(exception.getBindingResult());
	}

	public ResponseEntity<String> list(String tenantId) {
		logger.debug("Entering list()");
		HttpHeaders headers = new HttpHeaders(); 
		headers.set("tenantId", tenantId);
		headers.set("jwt", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlY21uZXh0YWRtaW4iLCJ1c2VySWQiOiI4OTc2MzIzIiwicm9sZSI6ImFjY291bnRhZG1pbiJ9.dKgYzDxmke505lhJA-basgbPpvtqFDpD79Bc2X24QjC5qTq0vKGD9IZG3-mL4YbN-QgUBLxVnh73N9tp9nd0zg");
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		//String url = eurekaUrlResolver.procureUrl(storageServiceId);
		ResponseEntity<String> response = restTemplate.exchange(storageurl + storageCredentialPath, HttpMethod.GET, request, String.class);
		logger.debug("Exit list()");
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}

	public ResponseEntity<String> readStorageCredential(String id,String tenantId) {
		logger.debug("Entering readStorageCredential()");
		HttpHeaders headers = new HttpHeaders(); 
		headers.set("tenantId", tenantId);
		headers.set("jwt", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlY21uZXh0YWRtaW4iLCJ1c2VySWQiOiI4OTc2MzIzIiwicm9sZSI6ImFjY291bnRhZG1pbiJ9.dKgYzDxmke505lhJA-basgbPpvtqFDpD79Bc2X24QjC5qTq0vKGD9IZG3-mL4YbN-QgUBLxVnh73N9tp9nd0zg");
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		//String url = eurekaUrlResolver.procureUrl(storageServiceId);
		ResponseEntity<String> response = restTemplate.exchange(storageurl + storageCredentialPath + "/" + id, HttpMethod.GET, request, String.class);
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}

	@ApiOperation(value = "Delete a Storage Credential")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Storage Credential deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> deleteStorageCredential(@PathVariable("id") String id,
			@RequestParam(value = "version", required = false) String version,String tenantId) {
		logger.debug("Entering deleteStorageCredential()");
		//String url = eurekaUrlResolver.procureUrl(storageServiceId);
		HttpHeaders headers = new HttpHeaders(); 
		headers.set("tenantId", tenantId);
		headers.set("jwt", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlY21uZXh0YWRtaW4iLCJ1c2VySWQiOiI4OTc2MzIzIiwicm9sZSI6ImFjY291bnRhZG1pbiJ9.dKgYzDxmke505lhJA-basgbPpvtqFDpD79Bc2X24QjC5qTq0vKGD9IZG3-mL4YbN-QgUBLxVnh73N9tp9nd0zg");
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(storageurl + storageCredentialPath + "/" + id);
		// Add query parameter
		if (version != null && !version.isEmpty()) {
			builder.queryParam("version", version);
		}
		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, request,
				String.class);
		logger.debug("Exit deleteStorageCredential()");
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}

	@ApiOperation(value = "Update Storage Credentials")
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Storage Credentials updated", response = StorageCredentials.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<String> updateStorageCredential(@RequestBody String updateParams,
			@PathVariable("id") String id, @RequestParam(value = "version", required = false) Long version,String tenantId) {
		logger.debug("Entering updateStorageCredential()");
		//String url = eurekaUrlResolver.procureUrl(storageServiceId);
		
		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(storageurl + storageCredentialPath + "/" + id);
		// Add query parameter
		if (version != null) {
			builder.queryParam("version", version);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(updateParams, headers);
		headers.set("tenantId", tenantId);
		headers.set("jwt", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlY21uZXh0YWRtaW4iLCJ1c2VySWQiOiI4OTc2MzIzIiwicm9sZSI6ImFjY291bnRhZG1pbiJ9.dKgYzDxmke505lhJA-basgbPpvtqFDpD79Bc2X24QjC5qTq0vKGD9IZG3-mL4YbN-QgUBLxVnh73N9tp9nd0zg");
		
		restTemplate.setErrorHandler(new RestResponseErrorHandler());
		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, request,
				String.class);

		logger.debug("Exit updateStorageCredential()");
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}

}
