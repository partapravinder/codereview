package com.newgen.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.constants.Constants;
import com.newgen.dto.BulkFolderDTO;
import com.newgen.dto.FolderDTO;
import com.newgen.enumdef.Privilege;
import com.newgen.exception.CustomException;
import com.newgen.model.AsyncFolderOperation;
import com.newgen.model.AsyncFolderOperation.Action;
import com.newgen.model.AsyncToken;
import com.newgen.model.CopyFolder;
import com.newgen.model.CopyToken;
import com.newgen.model.Folder;
import com.newgen.model.Lock;
import com.newgen.model.RedisQueue;
import com.newgen.model.ValidationError;
import com.newgen.service.AsyncFolderService;
import com.newgen.service.FolderService;
import com.newgen.service.LockService;
import com.newgen.validation.ValidationErrorBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/folders")
@Api(value = "Folder", description = "Operations for Folders")
public class FolderController extends ExceptionThrower implements Constants {
	private static final Logger logger = LoggerFactory.getLogger(FolderController.class);

	// @Value("${metadata.service.url}")
	// private String url;

	@Autowired
	LockService lockService;

	@Autowired
	AsyncFolderService asyncFolderService;

	@Autowired
	RedisQueue redisQueue;

	@Autowired
	private FolderService folderService;

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create a folder", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Cabinet created", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Folder> createFolder(@Valid @RequestBody FolderDTO folderDTO,
			@RequestHeader(value = "tenantId") String tenantId)
			throws CustomException, JsonParseException, JsonMappingException, IOException, JSONException {
		logger.debug("Entering createFolder()");
		Folder folder = null;
		String guid = UUID.randomUUID().toString();
		try {
			if ("folder".equalsIgnoreCase(folderDTO.getFolderType())) {
				// Check if parent folder id is null or empty
				if (folderDTO.getParentFolderId() == null || folderDTO.getParentFolderId().isEmpty()) {
					logger.debug("ParentFolderId is null or empty in the input");
					throwParentFolderBlankException();
				} else {
					// lock on the parentfolderId
					getSharedLock(folderDTO.getParentFolderId(), guid, tenantId);
					if (folderDTO.getPrivilege() == null) {
						System.out.println("FoldeDTO has privilege as null");
						folderDTO.setPrivilege(Privilege.INHERITED.toString());
					} else {
						try {
							folderDTO.setPrivilege(
									Privilege.valueOf(folderDTO.getPrivilege().toString().toUpperCase()).toString());
							System.out.println("FoldeDTO has privilege as " + folderDTO.getPrivilege());
						} catch (Exception e) {
							e.printStackTrace();
							throwThisPrivilegeIsNotValidException();
						}
					}

					// check if the parentfolderid exists in the system
					Folder parentFolder = folderService.findById(folderDTO.getParentFolderId(), tenantId);

					if (parentFolder != null) {
						logger.debug("ParentFolderId exists in the system: " + folderDTO.getParentFolderId());
						Date date = new Date();
						folder = new Folder(null, folderDTO.getFolderName().toString(), folderDTO.getFolderType(),
								folderDTO.getComments(), parentFolder.getId(), folderDTO.getOwnerName(),
								folderDTO.getOwnerId(), date, folderDTO.getUsedFor(), folderDTO.getMetadata(),
								tenantId);
						folder.setRevisedDateTime(date);
						folder.setPrivilege(Privilege.valueOf(folderDTO.getPrivilege()));
						// Create the folder
						folder = folderService.insert(folder);

					} else {
						logger.debug("ParentFolderId does not exist in the system: " + folderDTO.getParentFolderId());
						// if the parent folder does not exist
						// release lock on parentFolderId
						throwParentFolderNotFoundException();
					}
				}
			} else {
				logger.debug("Folder Type is invalid");
				throwFolderTypeInvalidException();
			}
		} finally {
			// Releasing locks in exception
			if (folderDTO.getParentFolderId() != null && !folderDTO.getParentFolderId().isEmpty()) {
				try {
					releaseLock(folderDTO.getParentFolderId(), tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + folderDTO.getParentFolderId());
				}
			}
		}
		logger.debug("Exit createFolder()");
		return new ResponseEntity<>(folder, HttpStatus.CREATED);
	}

	public void releaseLock(String id, String tenantId) throws CustomException {
		lockService.delete(id, tenantId);
	}

	public Lock getExclusiveLock(String id, String guid, String tenantId) throws CustomException {
		return lockService.getLock(id, guid, "exclusive", tenantId);
	}

	public Lock getSharedLock(String id, String guid, String tenantId) throws CustomException {
		return lockService.getLock(id, guid, "shared", tenantId);
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
	@ApiOperation(value = "Searching for Folders", notes = "Other fields may be included to further filter the list returned.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Folder list", response = Folder.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<Folder>> searchFolders(HttpServletRequest request) throws CustomException {
		logger.debug("Entering searchFolders()");
		String tenantId = request.getHeader("tenantId").toString();
		Map<String, String[]> allRequestParams = request.getParameterMap();
		List<Folder> folderList = folderService.search(allRequestParams, tenantId);
		logger.debug("Exiting searchFolders()");
		if (folderList.isEmpty())
			return new ResponseEntity<List<Folder>>(folderList, HttpStatus.NOT_FOUND);
		return new ResponseEntity<List<Folder>>(folderList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/pageNo/{pno}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Searching for Folders - Pagination", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get Content Status", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<Folder>> searchFoldersByPage(HttpServletRequest request,
			@PathVariable(value = "pno", required = true) int pno) throws CustomException, JSONException {
		logger.debug("Entering searchFoldersByPage()");
		String tenantId = request.getHeader("tenantId").toString();
		Map<String, String[]> allRequestParams = request.getParameterMap();
		List<Folder> folderList = folderService.searchByPage(allRequestParams, tenantId, pno);
		logger.debug("Exit searchFoldersByPage()");
		if (folderList.isEmpty())
			return new ResponseEntity<List<Folder>>(folderList, HttpStatus.NOT_FOUND);
		return new ResponseEntity<List<Folder>>(folderList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a folder", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Folder deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> deleteFolder(@PathVariable("id") String id,
			@RequestHeader(value = "tenantId") String tenantId,
			@RequestParam(value = VERSION_PARAM, required = false) String version,
			@RequestParam(value = "recursive", required = false) boolean recursive,
			@RequestParam(value = "isCabinet", required = false) boolean isCabinet)
			throws CustomException, JSONException, JsonParseException, JsonMappingException, IOException {
		logger.debug("Entering deleteFolder()");
		String lockId = null;
		String guid = UUID.randomUUID().toString();
		ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		Folder folder = (isCabinet) ? folderService.findCabinetById(id, tenantId)
				: folderService.findById(id, tenantId);
		if (folder != null) {

			// String parentFolderId = folder.getParentFolderId();

			try {
				// lock on the folder id
				lockId = id;
				getExclusiveLock(lockId, guid, tenantId);

				// Check if any subfolder or content exist under the folder
				logger.debug("Checking if any subfolder or contents exist with parentFolderId: " + id);
				if (folderService.isFolderEmpty(id, tenantId)) {
					folderService.delete(id, version, tenantId);
				} else {
					if (!recursive) {
						/*
						 * If the delete is not recursive then throw folder not empty exception
						 */
						logger.debug("Folder is not empty");
						throwFolderContentExistsException();
					} else {
						/*
						 * If the delete is recursive then queue the folder to be deleted asynchronously
						 */
						folderService.markDeleteFolder(id, null, tenantId);
						// folderService.markRecursiveFolder(id, tenantId);

						AsyncFolderOperation asyncFolderOperation = asyncFolderService
								.insert(new AsyncFolderOperation(Action.DELETE, id, null, new Date(), tenantId));
						if (asyncFolderOperation != null) {
							redisQueue.add(asyncFolderOperation.getId());
						}
					}
				}
			} finally {
				if (lockId != null && !lockId.isEmpty()) {
					try {
						releaseLock(lockId, tenantId);
					} catch (Exception e1) {
						logger.debug(e1.getMessage() + e1);
						logger.debug("Exception in releasing lock for id: " + lockId);
					}
				}
			}

		}
		logger.debug("Exit deleteFolder()");
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());

	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update Folder Info")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Folder updated", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<Folder> updateFolder(@RequestBody String updateFolderParams, @PathVariable("id") String id,
			@RequestHeader(value = "tenantId") String tenantId,
			@RequestParam(value = VERSION_PARAM, required = false) Long version) throws CustomException, JSONException {
		logger.debug("Entering updateFolder()");
		String lockId = null;
		Folder folder = null;
		String guid = UUID.randomUUID().toString();
		try {
			// lock on content id
			lockId = id;
			getSharedLock(lockId, guid, tenantId);

			JSONObject json = new JSONObject(updateFolderParams);

			if (json.has("privilege") && !(json.getString("privilege").isEmpty())) {
				String privilege = json.getString("privilege").toUpperCase();
				try {
					Privilege.valueOf(privilege);
				} catch (Exception e) {
					e.printStackTrace();
					throwThisPrivilegeIsNotValidException();
				}
				json.put("privilege", privilege);
			}

			folder = folderService.update(id, json.toString(), version, tenantId);
		} finally {
			if (lockId != null && !lockId.isEmpty()) {
				try {
					releaseLock(lockId, tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + lockId);
				}
			}
		}
		logger.debug("Exit updateFolder()");
		if (folder == null)
			throwUnknownErrorException();
		return new ResponseEntity<Folder>(folder, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/move/{id}/{targetFolderId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Move Folder")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Folder moved", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Folder> moveFolder(@PathVariable("id") String id,
			@PathVariable("targetFolderId") String targetFolderId, @RequestHeader(value = "tenantId") String tenantId,
			@RequestParam(value = VERSION_PARAM, required = false) Long version) throws CustomException, JSONException {
		logger.debug("Entering moveFolder()");
		Folder folder = null;
		folder = folderService.findById(id, tenantId);
		if (folder != null) {

			String lockId = null;
			String lockId2 = null;

			String guid = UUID.randomUUID().toString();
			try {
				// shared lock on target folderId
				lockId = targetFolderId;
				getSharedLock(lockId, guid, tenantId);

				// check if the target folder exists or not
				Folder targetFolder = folderService.findById(targetFolderId, tenantId);
				if (targetFolder != null) {
					// Shared lock on folder to be moved
					lockId2 = id;
					getSharedLock(lockId2, guid, tenantId);

					folder = folderService.moveFolder(id, targetFolderId, version, tenantId);
				} else {
					logger.debug("Tager Folder is not found with id: " + targetFolderId);
					throwTargetFolderNotFoundException();
				}
			} finally {
				if (lockId2 != null && !lockId2.isEmpty()) {
					try {
						releaseLock(lockId2, tenantId);
					} catch (Exception e1) {
						logger.debug(e1.getMessage() + e1);
						logger.debug("Exception in releasing lock for id: " + lockId2);
					}
				}
				if (lockId != null && !lockId.isEmpty()) {
					try {
						releaseLock(lockId, tenantId);
					} catch (Exception e1) {
						logger.debug(e1.getMessage() + e1);
						logger.debug("Exception in releasing lock for id: " + lockId);
					}
				}
			}
		} else {
			logger.debug("Source Folder is not found with id: " + id);
			throwSourceFolderNotFoundException();
		}
		logger.debug("Exit moveFolder()");
		return new ResponseEntity<Folder>(folder, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/copy/{id}/{targetFolderId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Copy Folder")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Folder moved", response = CopyToken.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<AsyncToken> copyFolder(@PathVariable("id") String id,
			@RequestHeader(value = "tenantId") String tenantId, @PathVariable("targetFolderId") String targetFolderId)
			throws CustomException, JSONException {
		logger.debug("Entering copyFolder()");
		AsyncToken copyToken = new AsyncToken();
		/* Check if source folder exists or not */
		if (folderService.findById(id, tenantId) != null) {
			/*
			 * Check if target folder is not a child of the source folder.
			 */
			if (folderService.checkIfFolderIsNotAChild(id, targetFolderId, tenantId)) {
				AsyncFolderOperation asyncFolderOperation = asyncFolderService
						.insert(new AsyncFolderOperation(Action.COPY, id, targetFolderId, new Date(), tenantId));
				if (asyncFolderOperation != null) {
					redisQueue.add(asyncFolderOperation.getId());
					// Set the guid to return copy folder token
					copyToken.setToken(asyncFolderOperation.getId());
				}
			} else {
				throwInvalidTargetFolderException();
			}
		} else {
			throwSourceFolderNotFoundException();
		}
		logger.debug("Exit copyFolder()");
		return new ResponseEntity<AsyncToken>(copyToken, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/copystatus", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Folder Copy Status", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get Content Status", response = CopyFolder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<AsyncFolderOperation> copyFolderStatus(@RequestHeader(value = "tenantId") String tenantId,
			@RequestParam(value = "token", required = true) String token) throws CustomException, JSONException {
		logger.debug("Entering copyFolderStatus()");
		AsyncFolderOperation asyncFolderOperation = null;
		asyncFolderOperation = asyncFolderService.findOne(token, tenantId);
		logger.debug("Exit copyFolderStatus()");
		if (asyncFolderOperation == null)
			return new ResponseEntity<AsyncFolderOperation>(asyncFolderOperation, HttpStatus.NOT_FOUND);
		return new ResponseEntity<AsyncFolderOperation>(asyncFolderOperation, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/children/{parentFolderId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Getting list of child elements in a folder ", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<String> getChildren(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("parentFolderId") String parentFolderId)
			throws CustomException, JsonParseException, JsonMappingException, JSONException, IOException {
		logger.debug("Entering getChildren()");
		if (folderService.findById(parentFolderId, tenantId) == null)
			throwFolderNotFoundException();
		String list = folderService.listChildElements(parentFolderId, tenantId);
		logger.debug("Exit getChildren()");
		if (list.equals(""))
			return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
		return new ResponseEntity<String>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "/bulk", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create bulk folder", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Folders created", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<Folder>> createBulkFolder(@RequestBody BulkFolderDTO bulkFolderDTO,
			@RequestHeader(value = "tenantId") String tenantId)
			throws CustomException, JsonParseException, JsonMappingException, IOException, JSONException {
		logger.debug("Entering createBulkFolder()");
		List<Folder> folderList = new ArrayList<Folder>();
		String guid = UUID.randomUUID().toString();
		try {
			if ("folder".equalsIgnoreCase(bulkFolderDTO.getFolderType())) {

				// Check if parent folder id is null or empty
				if (bulkFolderDTO.getParentFolderId() == null || bulkFolderDTO.getParentFolderId().isEmpty()) {
					logger.debug("ParentFolderId is null or empty in the input");
					throwParentFolderBlankException();
				} else {
					// lock on the parentfolderId
					getSharedLock(bulkFolderDTO.getParentFolderId(), guid, tenantId);

					// check if the parentfolderid exists in the system
					Folder parentFolder = folderService.findById(bulkFolderDTO.getParentFolderId(), tenantId);

					if (parentFolder != null) {
						logger.debug("ParentFolderId exists in the system: " + bulkFolderDTO.getParentFolderId());

						for (int i = 0; i < bulkFolderDTO.getFolderName().length; i++) {
							Date date = new Date();
							Folder folder = new Folder(null, bulkFolderDTO.getFolderName()[i].toString(),
									bulkFolderDTO.getFolderType(), bulkFolderDTO.getComments(),
									bulkFolderDTO.getParentFolderId(), bulkFolderDTO.getOwnerName(),
									bulkFolderDTO.getOwnerId(), date, bulkFolderDTO.getUsedFor(),
									bulkFolderDTO.getMetadata(), tenantId);
							folder.setRevisedDateTime(date);
							if (bulkFolderDTO.getPrivilege() == null) {
								System.out.println("FoldeDTO has privilege as null");
								folder.setPrivilege(Privilege.INHERITED);
							} else {
								try {
									folder.setPrivilege(Privilege.valueOf(bulkFolderDTO.getPrivilege()));
									System.out.println("FoldeDTO has privilege as " + bulkFolderDTO.getPrivilege());
								} catch (Exception e) {
									e.printStackTrace();
									throwThisPrivilegeIsNotValidException();
								}
							}

							folder = folderService.insert(folder);
							folderList.add(folder);
						}
					} else {
						logger.debug(
								"ParentFolderId does not exist in the system: " + bulkFolderDTO.getParentFolderId());
						// if the parent folder does not exist
						// release lock on parentFolderId
						throwParentFolderNotFoundException();
					}
				}
			} else {
				logger.debug("Folder Type is invalid");
				throwFolderTypeInvalidException();
			}
		} finally {
			// Releasing locks in exception
			if (bulkFolderDTO.getParentFolderId() != null && !bulkFolderDTO.getParentFolderId().isEmpty()) {
				try {
					releaseLock(bulkFolderDTO.getParentFolderId(), tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + bulkFolderDTO.getParentFolderId());
				}
			}
		}
		logger.debug("Exit createBulkFolder()");
		return new ResponseEntity<List<Folder>>(folderList, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Folder by Id", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get Content Status", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Folder> fetchFolderById(@PathVariable("id") String folderId,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException {
		logger.debug("Entering fetchFolderById()");
		Folder folder = null;
		folder = folderService.findById(folderId, tenantId);
		logger.debug("Exit fetchFolderById()");
		if (folder == null)
			throwFolderNotFoundException();
		return new ResponseEntity<Folder>(folder, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/metadata/{folderId}")
	@ApiOperation(value = "Delete Folder Metadata by Id", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Folder Metadata deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<Folder> deleteContentMetadata(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("folderId") String folderId) throws CustomException {
		logger.debug("Entering deleteContentMetadata()");
		if (folderService.findById(folderId, tenantId) == null)
			throwFolderNotFoundException();
		Folder folder = null;
		folder = folderService.update(folderId, null, null, tenantId);
		logger.debug("Exit deleteContentMetadata()");
		return new ResponseEntity<Folder>(folder, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/metadata/{folderId}")
	@ApiOperation(value = "GET Folder Metadata by Id", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Folder Metadata Retrieved"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<String> getFolderMetadata(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("folderId") String folderId) throws CustomException {
		logger.debug("Entering getFolderMetadata()");
		Folder folder = null;
		folder = folderService.findById(folderId, tenantId);
		if (folder == null)
			throwFolderNotFoundException();
		if (folder.getMetadata() == null)
			throwMetadataAbsentException();
		JSONObject folder_metadata = new JSONObject(folder.getMetadata());
		logger.debug("Exit getFolderMetadata()");
		return new ResponseEntity<String>(folder_metadata.toString(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/metadata")
	@ApiOperation(value = "Search Folders based on Metadata", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Folder Metadata Search"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<List<Folder>> searchFolderMetadata(@RequestHeader(value = "tenantId") String tenantId,
			@RequestBody String searchParams) throws CustomException, JSONException {
		logger.debug("Entering searchFolderMetadata()");
		List<Folder> folderList = folderService.searchByMetadata(searchParams, tenantId);
		logger.debug("Exiting searchFolderMetadata()");
		if (folderList.isEmpty())
			return new ResponseEntity<List<Folder>>(folderList, HttpStatus.NOT_FOUND);
		return new ResponseEntity<List<Folder>>(folderList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/rootpathhierarchy/{folderId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Getting root folder for given folder", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<String> getRootFolder(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("folderId") String folderId) throws CustomException {
		logger.debug("Entering getRootFolder()");
		if (folderService.findById(folderId, tenantId) == null)
			throwFolderNotFoundException();
		JSONObject hierarchy_json = new JSONObject();
		JSONArray folder_arr = new JSONArray();
		Folder folder = null;
		do {
			folder = folderService.findById(folderId, tenantId);
			if (folder != null && !folder.getFolderType().toString().equalsIgnoreCase("cabinet")) {
				JSONObject folder_json_temp = new JSONObject(folder);
				folder_arr.put(folder_json_temp);
				folderId = folder.getParentFolderId().toString();
			}
		} while (!folder.getFolderType().toString().equalsIgnoreCase("cabinet"));
		JSONObject cabinet_json = new JSONObject(folder);
		hierarchy_json.put("folder", folder_arr);
		hierarchy_json.put("cabinet", cabinet_json);
		logger.debug("Exit getRootFolder()");
		return new ResponseEntity<String>(hierarchy_json.toString(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/group/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Searching for Folders in Groups", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get folder", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<Folder>> searchFoldersByGroup(HttpServletRequest request, @PathVariable("id") String id)
			throws CustomException, JSONException, IOException {
		logger.debug("Entering searchFoldersByPage()");
		String tenantId = request.getHeader("tenantId");
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		List<String> userIds = folderService.userIdByGroup(id, tenantId);

		List<Folder> folderList = folderService.listFoldersUnderParentFolderIdInGroup(userIds, tenantId);

		logger.debug("Exit searchFoldersByPage()");
		return new ResponseEntity<List<Folder>>(folderList, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/private/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Searching for Folders in Shared Access", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get folder", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<Folder>> searchFoldersByShared(HttpServletRequest request, @PathVariable("id") String id)
			throws CustomException, JSONException, IOException {
		logger.debug("Entering searchFoldersByPage()");
		String tenantId = request.getHeader("tenantId");
		if (tenantId == null) {
			throwInvalidTenantException();
		}

		List<String> folderIds = folderService.userIdByShared(id, tenantId);

		List<Folder> folderList = folderService.findByPrivateFolderId(folderIds, tenantId);

		List<String> folderIdsofGorup = folderService.folderIdBySharedGroup(id, tenantId);
		List<Folder> folderListofGroupShare = folderService.findByPrivateFolderId(folderIdsofGorup, tenantId);
		folderList.addAll(folderListofGroupShare);

		logger.debug("Exit searchFoldersByPage()");
		return new ResponseEntity<List<Folder>>(folderList, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getLastModifiedFolder/{parentFolderId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Sorting Folders based on revised date time", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get folder", response = Folder.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Folder> getLastModifiedFolder(HttpServletRequest request,
			@PathVariable("parentFolderId") String parentFolderId, @RequestHeader(value = "tenantId") String tenantId)
			throws CustomException, JSONException, IOException {
		logger.debug("Entering getLastModifiedFolderForParentFolderId()");
		if (request.getHeader("tenantId") == null) {
			throwInvalidTenantException();
		}

		Folder folder = folderService.getLastModifiedFolder(parentFolderId, tenantId);

		logger.debug("Exit getLastModifiedFolderForParentFolderId()");
		return new ResponseEntity<Folder>(folder, HttpStatus.OK);
	}
}
