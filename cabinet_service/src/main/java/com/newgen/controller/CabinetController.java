package com.newgen.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.newgen.dto.FolderDTO;
import com.newgen.exception.CustomException;
//import com.newgen.model.AsyncFolderOperation;
import com.newgen.model.Folder;
import com.newgen.model.Lock;
import com.newgen.model.ValidationError;
import com.newgen.service.FolderService;
import com.newgen.service.LockService;
import com.newgen.validation.ValidationErrorBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/cabinets")
@Api(value = "Cabinet", description = "Operations for Cabinets")
public class CabinetController extends ExceptionThrower {
	
	private static final Logger logger = LoggerFactory.getLogger(CabinetController.class);

	@Autowired
	FolderService folderService;

	@Autowired
	LockService lockService;

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create a cabinet", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Cabinet created", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Folder> createCabinet(@Valid @RequestBody FolderDTO folderDTO,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException {
		logger.debug("Entering createCabinet()");
		Folder folder = null;
		Date date = new Date();
		folder = new Folder(null, folderDTO.getFolderName().toString(), folderDTO.getFolderType(),
				folderDTO.getComments(), null, folderDTO.getOwnerName(), folderDTO.getOwnerId(), date,
				folderDTO.getUsedFor(), folderDTO.getMetadata(), tenantId);
		folder.setAccessDateTime(date);
		folder.setRevisedDateTime(date);
		folder = folderService.insert(folder);
		logger.debug("Exit createCabinet()");
		if (folder == null)
			throwFolderCreationException();
		return new ResponseEntity<Folder>(folder, HttpStatus.CREATED);
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
	@ApiOperation(value = "Searching for Cabinets", notes = "Other fields may be included to further filter the list returned.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Cabinet list", response = Folder.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<Folder>> searchCabinets(HttpServletRequest request)
			throws CustomException, JSONException {
		logger.debug("Entering searchCabinets()");
		String tenantId = request.getHeader("tenantId").toString();
		List<Folder> folderList = folderService.search(request.getParameterMap(), tenantId);
		logger.debug("Exit searchCabinets()");
		if(folderList.isEmpty())
			return new ResponseEntity<List<Folder>>(folderList, HttpStatus.NOT_FOUND);
		return new ResponseEntity<List<Folder>>(folderList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/pageNo/{pno}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Cabinets by Page No.", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get Content Status", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<Folder>> searchCabinetsByPage(HttpServletRequest request,
			@PathVariable(value = "pno", required = true) int pno) throws CustomException {
		logger.debug("Entering searchCabinetsByPage()");
		String tenantId = request.getHeader("tenantId").toString();
		List<Folder> folderList = folderService.searchByPage(request.getParameterMap(), tenantId, pno);
		logger.debug("Exit searchCabinetsByPage()");
		if(folderList.isEmpty())
			return new ResponseEntity<List<Folder>>(folderList, HttpStatus.NOT_FOUND);
		return new ResponseEntity<List<Folder>>(folderList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a cabinet", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Cabinet deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> deleteCabinet(@PathVariable("id") String id,
			@RequestHeader(value = "tenantId") String tenantId,
			@RequestParam(value = "version", required = false) String version,
		@RequestParam(value = "recursive", required = false) boolean recursive)throws CustomException, JSONException {
		logger.debug("Entering deleteCabinet()");
		folderService.deleteCabinet(id, version, tenantId);
		logger.debug("Exit deleteCabinet()");
		return new ResponseEntity<String>(id, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update Cabinet Info")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Cabinet updated", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<Folder> updateCabinet(@RequestBody Map<String, String> updateFolderParams,
			@PathVariable("id") String id, @RequestParam(value = "version", required = false) Long version,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException, JSONException {
		logger.debug("Entering updateCabinet()");
		Folder folder = null;
		if (checkIfCabinet(id, tenantId)) {
			folder = folderService.update(id, updateFolderParams, version, tenantId);
		} else {
			throwInvalidCabinetException();
		}
		logger.debug("Exit updateCabinet()");
		if(folder==null) throwUnknownErrorException();
		return new ResponseEntity<Folder>(folder, HttpStatus.OK);
	}

	public Lock getExclusiveLock(String id, String guid, String tenantId) throws CustomException {
		return lockService.getLock(id, guid, "exclusive", tenantId);
	}

	public void releaseLock(String id, String tenantId) throws CustomException {
		lockService.delete(id, tenantId);
	}

	public boolean checkIfCabinet(String id, String tenantId) throws CustomException, JSONException {
		Folder folder = folderService.findById(id, tenantId);
		if (folder != null) {
			return true;
		}
		else {
			throwUnknownErrorException();
		}
		return false;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Cabinet by Id", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get Cabinet by Id", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Folder> fetchCabinetById(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("id") String folderId) throws CustomException, JSONException {
		logger.debug("Entering fetchCabinetById()");
		Folder folder = null;
		folder = folderService.findById(folderId, tenantId);
		logger.debug("Exit fetchCabinetById()");
		if(folder==null) throwUnknownErrorException();
		return new ResponseEntity<Folder>(folder, HttpStatus.OK);
	}

}
