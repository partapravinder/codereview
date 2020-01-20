package com.newgen.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

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

import com.microsoft.azure.storage.StorageException;
import com.newgen.dto.FTSSearchDTO;
import com.newgen.dto.StoreContentDTO;
import com.newgen.exception.CustomException;
import com.newgen.model.DownloadUrl;
import com.newgen.model.RedisQueue;
import com.newgen.model.StorageLocation;
import com.newgen.model.StorageLocationList;
import com.newgen.model.StorageProcess;
import com.newgen.model.StorageProcess.Action;
import com.newgen.model.StorageProcess.Status;
import com.newgen.model.StoreAction;
import com.newgen.model.StoreToken;
import com.newgen.model.StoreTokenSync;
import com.newgen.model.ValidationError;
import com.newgen.service.StorageCredentialService;
import com.newgen.service.StorageProcessService;
import com.newgen.service.StorageService;
import com.newgen.validation.ValidationErrorBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/store")
@Api(value = "Storage", description = "Operations for storing contents")
public class StorageController extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(StorageController.class);

	@Autowired
	StorageService storageService;

	@Autowired
	StorageProcessService storageProcessService;

	@Autowired
	StorageCredentialService storageCredentialService;

	@Autowired
	RedisQueue redisQueue;

	@RequestMapping(method = RequestMethod.POST, value = "/upload", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Store Content", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Content Stored", response = StoreToken.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public StoreToken storeContent(@Valid @RequestBody StoreContentDTO storeContentDTO,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException {
		logger.debug("Entering storeContent()");
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		StoreToken storeToken = new StoreToken();
		StorageProcess storageProcess = new StorageProcess(null, Status.PENDING, Action.UPLOAD,
				new StoreAction(storeContentDTO.getContentPath(), storeContentDTO.getStorageCredentialId(),
						storageCredentialService.findById(storeContentDTO.getStorageCredentialId(), tenantId).getContainerName(),
						null, null, storeContentDTO.getType(), tenantId),
				new Date(), tenantId);
		storageProcess = storageProcessService.insert(storageProcess, tenantId);
		redisQueue.add(storageProcess.getId());

		storeToken.setToken(storageProcess.getId());
		logger.debug("[Token " + storageProcess.getId() + "] successfully added to redis queue and storage process created");
		logger.debug("Exit storeContent()");
		return storeToken;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/uploadSync", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Store Content Synchronously", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Content Stored", response = StoreToken.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public StoreTokenSync storeContentSync(@Valid @RequestBody StoreContentDTO storeContentDTO,
			@RequestHeader(value = "tenantId") String tenantId) throws Exception {
		logger.debug("Entering storeContentSync()");
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		StoreTokenSync storeToken = new StoreTokenSync();

		String documentId = storageService.getDocumentId(storeContentDTO.getContentPath(), tenantId);
		
		StorageLocation storageLocation = storageService.upload(storeContentDTO.getContentPath(),
				storeContentDTO.getStorageCredentialId(), storeContentDTO.getType(), tenantId, documentId);

		StorageProcess storageProcess = new StorageProcess(null, Status.ACKNOWLEDGED, Action.UPLOAD,
				new StoreAction(storeContentDTO.getContentPath(), storeContentDTO.getStorageCredentialId(),
						storageCredentialService.findById(storeContentDTO.getStorageCredentialId(), tenantId).getContainerName(),
						null, null, storeContentDTO.getType(), tenantId),
				new Date(), tenantId);
		storageProcess.setStorageLocation(storageLocation);
		storageProcess.setStatusChangeDateTime(new Date());

		storageProcess = storageProcessService.insert(storageProcess, tenantId);

		storeToken.setToken(storageProcess.getId());
		storeToken.setStoragelocationid(storageLocation.getId());
		logger.debug("Exit storeContentSync()");
		return storeToken;
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ValidationError handleException(MethodArgumentNotValidException exception) {
		return createValidationError(exception);
	}

	private ValidationError createValidationError(MethodArgumentNotValidException exception) {
		return ValidationErrorBuilder.fromBindingErrors(exception.getBindingResult());
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
	@ApiOperation(value = "Delete Content")
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Content Deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public StoreToken deleteContent(@PathVariable("id") String id,
			@RequestParam(value = "version", required = false) String version,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException {
		logger.debug("Entering deleteContent()");
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		StoreToken storeToken = new StoreToken();
		StorageProcess storageProcess = new StorageProcess(null, Status.PENDING, Action.DELETE,
				new StoreAction(null, null, null, id, version, null, tenantId), new Date(), tenantId);
		storageProcess = storageProcessService.insert(storageProcess, tenantId);

		redisQueue.add(storageProcess.getId());
		storeToken.setToken(storageProcess.getId());
		logger.debug("[TokenDelete " + storageProcess.getId()
				+ "] successfully added to redis queue and storage process created");
		logger.debug("Exit deleteContent()");
		return storeToken;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/retrieve/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Retrieve Content", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retrieve Content", response = DownloadUrl.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public DownloadUrl retrieveContent(@PathVariable("id") String id,
			@RequestHeader(value = "tenantId") String tenantId)
			throws CustomException, InvalidKeyException, StorageException, URISyntaxException {
		logger.debug("Entering retrieveContent()");
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		DownloadUrl response = new DownloadUrl();
		response.setDownloadUrl(storageService.retreiveContent(id, tenantId));
		logger.debug("Exit retrieveContent()");
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/search/storage/ids", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Retrieve Content Ids", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retrieve Content", response = DownloadUrl.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<StorageLocationList> retrieveStorageIds(@RequestHeader(value="tenantId") String tenantId,
			@RequestBody List<String> params)
			throws CustomException, InvalidKeyException, StorageException, URISyntaxException {
		logger.debug("Entering retrieveStorageIds()");
		if (tenantId == null) {
			throwInvalidTenantException();
		}
		List<StorageLocation> searchResults = storageService.retreiveStorageIds(params,tenantId);
		StorageLocationList storageLocationList = new StorageLocationList();
		storageLocationList.setStorageLocationList(searchResults);
		logger.debug("Exit retrieveStorageIds()");
		return new ResponseEntity<>(storageLocationList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/status", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Content Status", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get Content Status", response = StorageProcess.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public StorageProcess contentStatus(@RequestParam(value = "token", required = true) String token,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException {
		if (tenantId == null) {
			throwInvalidTenantException();
		}
		logger.debug("Entering contentStatus()");
		// StorageProcess storageProcess =
		// storageProcessService.findOne(token,tenantId);
		StorageProcess storageProcess = storageProcessService.findOnestatus(token, tenantId);
		logger.debug("Exit contentStatus()");
		return storageProcess;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/acknowledge", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Commit Content Status", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Acknowledge Content Status", response = StorageProcess.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public StorageProcess acknowledgeContentStatus(@RequestParam(value = "token", required = true) String token,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException {
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		logger.debug("Entering acknowledgeContentStatus()");
		StorageProcess storageProcess = storageProcessService.updateStatus(token, Status.ACKNOWLEDGED, null, tenantId);
		logger.debug("Exit acknowledgeContentStatus()");
		return storageProcess;
	}

}
