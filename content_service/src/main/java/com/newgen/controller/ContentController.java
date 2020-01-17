package com.newgen.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newgen.dto.FTSSearchDTO;
import com.newgen.easysearch.EasySearch;
import com.newgen.enumdef.Privilege;
import com.newgen.enumdef.Versioning;
import com.newgen.exception.CustomException;
import com.newgen.model.Content;
import com.newgen.model.ContentLocation;
import com.newgen.model.DownloadUrlResponse;
import com.newgen.model.SearchResults;
import com.newgen.model.StorageProcess;
import com.newgen.model.ValidationError;
import com.newgen.repository.ContentRepository;
import com.newgen.service.ContentLocationService;
import com.newgen.service.ContentService;
import com.newgen.validation.ValidationErrorBuilder;
import com.newgen.wrapper.service.WrapperService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/contents")
@Api(value = "Content", description = "Operations for Contents")
public class ContentController extends ExceptionThrower {
	private static final Logger logger = LoggerFactory.getLogger(ContentController.class);

	//@Autowired
	EasySearch easySearch;// = new AzureSearch("globalsearch");

	@Autowired
	private WrapperService wrapperService;

	@Autowired
	private ContentLocationService contentLocationService;

	@Autowired
	ContentService contentService;

	@Autowired
	ContentRepository contentRepository;

	// Save the uploaded file to this folder
	@Value("${upload.folder}")
	private String UPLOADED_FOLDER;

	// If easy Search has been set up
	@Value("${easysearch.enabled}")
	private boolean EASYSEARCH_ENABLED;

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create content", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Content created", response = Content.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> uploadContent(@RequestParam("file") MultipartFile file,
			@RequestParam("name") String name, @RequestParam("contentType") String contentType,
			@RequestParam(value = "comments", required = false) String comments,
			@RequestParam(value = "parentFolderId") String parentFolderId,
			@RequestParam(value = "ownerName") String ownerName, @RequestParam(value = "ownerId") String ownerId,
			@RequestParam(value = "noOfPages") String noOfPages,
			@RequestParam(value = "documentSize") String documentSize,
			@RequestParam(value = "documentType") String documentType,
			@RequestParam(value = "storageCredentialId") String storageCredentialId,
			@RequestParam(value = "metadata", required = false) String metadata,
			@RequestParam(value = "dataclass", required = false) String dataclass,
			@RequestParam(value = "priviledge", required = false) String privilege,
			@RequestHeader(value = "tenantId") String tenantId)
			throws CustomException, JsonProcessingException, InterruptedException, JSONException {
		logger.debug("Entering uploadContent()");

		if (tenantId == null) {
			throwInvalidTenantException();
		}

		ResponseEntity<String> response = null;
		Content content = null;
		// Check if the file is empty or not
		if (file.isEmpty()) {
			throwFileEmptyException();
		} else {
			try {
				String org_file_name = file.getOriginalFilename();
				String unique_file_name = null;
				String guid1 = UUID.randomUUID().toString();

				if (org_file_name.contains(".")) {
					String base_name = org_file_name.substring(0, org_file_name.indexOf("."));
					String ext = org_file_name.substring(org_file_name.indexOf("."), org_file_name.length());
					unique_file_name = base_name + guid1 + ext;
				} else {
					unique_file_name = org_file_name + guid1;
				}
				// Get the file and save it in temp folder
				byte[] bytes = file.getBytes();

				String uploadPath = UPLOADED_FOLDER + unique_file_name;
				Path path = Paths.get(uploadPath);
				Files.write(path, bytes);

				response = wrapperService.uploadStoreContent(file, name, contentType, comments, parentFolderId,
						ownerName, ownerId, storageCredentialId, uploadPath, true, tenantId);

				logger.debug("response.statusCodeUploadStoreContent=>" + response.getStatusCode());
				if (response.getStatusCode() == HttpStatus.CREATED) {
					logger.debug("response.statusCodeUploadMetaContent=>" + response.getStatusCode());
					JSONObject jsonObj = new JSONObject(response.getBody());
					String token = jsonObj.get("token").toString();
					logger.debug("[Token " + token + "] Content uploaded to cloud, about to create content metadata");

					if (privilege == null || privilege.isEmpty() || privilege.trim().equals("")) {
						System.out.println("ContentDTO has privilege as null");
						privilege = Privilege.INHERITED.toString();
					} else {
						try {
							Privilege.valueOf(privilege.toUpperCase()).toString();
						} catch (Exception e) {
							e.printStackTrace();
							throwThisPrivilegeIsNotValidException();
						}
					}

					content = wrapperService.uploadMetaContent(file, name, contentType, comments, parentFolderId,
							ownerName, ownerId, storageCredentialId, token, noOfPages, documentSize, documentType,
							metadata, Privilege.valueOf(privilege), true, null, tenantId, true, dataclass);
					// Send the response as an output
					if (content != null) {
						logger.debug("[Token " + token + "] Content metadata created " + response.getBody());
						jsonObj = new JSONObject(response.getBody());
						String contentId = content.getId();

						JSONObject responseJsonObject = new JSONObject();
						responseJsonObject.put("id", contentId);
						responseJsonObject.put("status", "PENDING");
						logger.debug("[Token " + token + "] Response for upload content api "
								+ responseJsonObject.toString());
						if (EASYSEARCH_ENABLED) {
							// Content upload to Azure search engine!!!
							easySearch.postSearchData(content.getId(), name, contentType, comments, parentFolderId,
									ownerName, ownerId, noOfPages, documentSize, documentType, tenantId, metadata);
							// End Content upload to Azure search engine!!!
						}
						return new ResponseEntity<>(responseJsonObject.toString(), response.getStatusCode());
					}
				} else {
					throwContentUploadFailed();
				}
			} catch (IOException e) {
				logger.debug(e.getMessage(), e);
				e.printStackTrace();
				throwFileStoreException();
			}
			logger.debug("Exit uploadContent()");
		}
		if (response == null) {
			return new ResponseEntity<>("Failed to upload Content", HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<>(response.getBody(), response.getStatusCode());
		}

	}

	@RequestMapping(value = "/sync", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create content Sync", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Content created", response = Content.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Content> uploadContentSync(@RequestParam("file") MultipartFile file,
			@RequestParam("name") String name, @RequestParam("contentType") String contentType,
			@RequestParam(value = "comments", required = false) String comments,
			@RequestParam(value = "parentFolderId") String parentFolderId,
			@RequestParam(value = "ownerName") String ownerName, @RequestParam(value = "ownerId") String ownerId,
			@RequestParam(value = "noOfPages") String noOfPages,
			@RequestParam(value = "documentSize") String documentSize,
			@RequestParam(value = "documentType") String documentType,
			@RequestParam(value = "storageCredentialId") String storageCredentialId,
			@RequestParam(value = "metadata", required = false) String metadata,
			@RequestParam(value = "dataclass", required = false) String dataclass,
			@RequestParam(value = "priviledge", required = false) String privilege,
			@RequestHeader(value = "tenantId") String tenantId)
			throws CustomException, InterruptedException, JSONException, IOException {
		logger.debug("Entering uploadContentSync()");
		ResponseEntity<String> response = null;
		Content content = null;

		// Check if the file is empty or not
		if (file.isEmpty()) {
			throwFileEmptyException();
		} else {
			try {

				String org_file_name = file.getOriginalFilename();
				String unique_file_name = null;
				String guid1 = UUID.randomUUID().toString();

				if (org_file_name.contains(".")) {
					String base_name = org_file_name.substring(0, org_file_name.indexOf("."));
					String ext = org_file_name.substring(org_file_name.indexOf("."), org_file_name.length());
					unique_file_name = base_name + guid1 + ext;
				} else {
					unique_file_name = org_file_name + guid1;
				}

				// Get the file and save it in temp folder
				byte[] bytes = file.getBytes();

				String uploadPath = UPLOADED_FOLDER + unique_file_name;
				Path path = Paths.get(uploadPath);
				Files.write(path, bytes);

				response = wrapperService.uploadStoreContent(file, name, contentType, comments, parentFolderId,
						ownerName, ownerId, storageCredentialId, uploadPath, false, tenantId);

				logger.debug("response.statusCodeUploadStoreContentSync=>" + response.getStatusCode());
				if (response.getStatusCode() == HttpStatus.CREATED) {

					logger.debug("response.statusCodeUploadMetaContent=>" + response.getStatusCode());
					JSONObject jsonObj = new JSONObject(response.getBody());
					String token = jsonObj.get("token").toString();
					String locationId_temp = jsonObj.get("storagelocationid").toString();

					logger.debug("[Token " + token + "] Content uploaded to cloud, about to create content metadata");

					if (privilege == null || privilege.isEmpty() || privilege.trim().equals("")) {
						System.out.println("ContentDTO has privilege as null");
						privilege = Privilege.INHERITED.toString();
					} else {
						try {
							Privilege.valueOf(privilege.toUpperCase()).toString();
						} catch (Exception e) {
							e.printStackTrace();
							throwThisPrivilegeIsNotValidException();
						}
					}

					content = wrapperService.uploadMetaContent(file, name, contentType, comments, parentFolderId,
							ownerName, ownerId, storageCredentialId, null, noOfPages, documentSize, documentType,
							metadata, Privilege.valueOf(privilege), false, locationId_temp, tenantId, true, dataclass);
					// Send the response as an output
					if (content != null) {
						logger.debug("[Token " + token + "] Content metadata created " + response.getBody());
						content.setToken("");
						logger.debug("EASYSEARCH_ENABLED--->" + EASYSEARCH_ENABLED);
						if (EASYSEARCH_ENABLED) {
							logger.debug("EASYSEARCH_ENABLED--->" + EASYSEARCH_ENABLED);
							// Content upload to Azure search engine!!!
							easySearch.postSearchData(content.getId(), name, contentType, comments, parentFolderId,
									ownerName, ownerId, noOfPages, documentSize, documentType, tenantId, null);
							// End Content upload to Azure search engine!!!
						}
						// return new ResponseEntity<>(responseJsonObject.toString(),
						// response.getStatusCode());
					}
				} else {
					throwContentUploadFailed();
				}
			} catch (IOException e) {
				logger.debug(e.getMessage(), e);
				e.printStackTrace();
				throwFileStoreException();
			}
			logger.debug("Exit uploadContentSync()");
		}
		if (response == null && content == null) {
			throwContentUploadFailed();
		}
		return new ResponseEntity<>(content, HttpStatus.CREATED);
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ValidationError handleException(MethodArgumentNotValidException exception) {
		return createValidationError(exception);
	}

	private ValidationError createValidationError(MethodArgumentNotValidException exception) {
		return ValidationErrorBuilder.fromBindingErrors(exception.getBindingResult());
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@ApiOperation(value = "Delete a content")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Content deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<String> deleteContent(@PathVariable("id") String id,
			@RequestParam(value = "version", required = false) BigDecimal version,
			@RequestHeader(value = "tenantId") String tenantId) throws CustomException {
		logger.debug("Entering deleteContent()");
		logger.debug("Content to delete: " + id);
		wrapperService.deleteContent(id, version, tenantId);

		if (EASYSEARCH_ENABLED) {
			// Content delete from Azure search engine!!!
			easySearch.deleteSearchData(id, tenantId);
			// End Content delete from Azure search engine!!!
		}

		logger.debug("Exit deleteContent()");
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/parent/{id}")
	@ApiOperation(value = "Delete a content by Parent ")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Content deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<String> deleteContentByParent(@PathVariable("id") String id,
			@RequestParam(value = "version", required = false) BigDecimal version,
			@RequestHeader(value = "tenantId") String tenantId) throws JSONException, CustomException {
		logger.debug("Entering deleteContent()");
		logger.debug("Parent FolderId : " + id);
		if (tenantId == null) {
			throwInvalidTenantException();
		}
		// Get Content Details
		List<Content> contents = new ArrayList<Content>();
		contents = contentService.listContentsForParentFolderId(id, tenantId);
		if (contents != null) {
			for (Content content : contents) {
				id = content.getId();
				contentService.markDeleteContent(id, version, tenantId);
			}
		}
		return new ResponseEntity<String>("", HttpStatus.NO_CONTENT);
	}

	@RequestMapping(method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Searching for Content")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Contents found", response = Content.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<List<Content>> searchContents(HttpServletRequest request) throws CustomException {
		logger.debug("Entering searchContents()");
		String tenantId = request.getHeader("tenantId").toString();
		List<Content> contentList = wrapperService.searchContents(request, tenantId);
		logger.debug("Exit searchContents()");
		if (contentList.isEmpty())
			return new ResponseEntity<>(contentList, HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(contentList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/pageNo/{pno}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Searching for Content by PageNo")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Contents found", response = Content.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<List<Content>> searchContentsByPage(HttpServletRequest request,
			@PathVariable(value = "pno", required = true) int pno) {
		logger.debug("Entering searchContentsByPage()");
		String tenantId = request.getHeader("tenantId").toString();
		List<Content> contentList = wrapperService.searchContentsByPage(request, pno, tenantId);
		logger.debug("Exit searchContentsByPage()");
		if (contentList.isEmpty())
			return new ResponseEntity<>(contentList, HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(contentList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update Content Info")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Content updated", response = Content.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<Content> updateContentInfo1(@RequestBody String updateContentParams,
			@PathVariable("id") String id, @RequestParam(value = "version", required = false) BigDecimal version,
			@RequestHeader(value = "tenantId") String tenantId) throws JSONException, CustomException {
		logger.debug("Entering updateContentInfo()");
		Content content = null;
		content = wrapperService.updateContentInfo(updateContentParams, id, version, tenantId);
		logger.debug("Exit updateContentInfo()");
		if (content == null)
			throwUnknownErrorException();
		return new ResponseEntity<>(content, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{id}", params = "parentFolderId", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Copy Content")
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Content copied", response = Content.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Content> copyContent(@RequestParam("parentFolderId") String parentFolderId,
			@RequestHeader(value = "tenantId") String tenantId, @PathVariable("id") String id)
			throws JSONException, CustomException {
		logger.debug("Entering copyContent()");
		Content copiedContent = null;
		copiedContent = contentService.copyContent(parentFolderId, id, tenantId);
		logger.debug("Exit copyContent()");
		if (copiedContent == null)
			throwUnknownErrorException();
		return new ResponseEntity<Content>(copiedContent, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/move/{id}/{targetFolderId}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Move Content")
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Content moved", response = Content.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Content> moveContent(@PathVariable("id") String id,
			@PathVariable("targetFolderId") String targetFolderId,
			@RequestParam(value = "version", required = false) BigDecimal version,
			@RequestHeader(value = "tenantId") String tenantId) throws JSONException, CustomException {
		logger.debug("Entering moveContent()");
		Content content = null;
		content = contentService.moveContent(id, targetFolderId, version, tenantId);
		logger.debug("Exit moveContent()");
		if (content == null)
			throwUnknownErrorException();
		return new ResponseEntity<Content>(content, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/status", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Content Status", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get Content Status", response = StorageProcess.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> contentStatus(@RequestHeader(value = "tenantId") String tenantId,
			@RequestParam(value = "id", required = true) String id) throws CustomException, JSONException {
		logger.debug("Entering contentStatus()");
		Content content = null;
		content = contentService.findUncommitedById(id, tenantId);
		JSONObject obj = new JSONObject();
		if (content != null) {
			if ("uncommitted".equalsIgnoreCase(content.getFlag().toString()) && content.getToken() != null
					&& !content.getToken().isEmpty()) {
				String token = content.getToken().toString();
				// Send the token to the storage service to fetch the status
				String status = getContentUploadStatus(token, tenantId);
				obj = new JSONObject();
				obj.put("status", status);
			} else {
				obj.put("status", "COMPLETED");
			}
		}
		logger.debug("Exit contentStatus()");
		return new ResponseEntity<>(obj.toString(), HttpStatus.OK);
	}

	private String getContentUploadStatus(String token, String tenantId) throws JSONException {
		if (token != null && !token.isEmpty()) {
			ResponseEntity<String> response = wrapperService.getContentUploadStatus(token, tenantId);
			if (response.getStatusCode() == HttpStatus.OK) {
				JSONObject jsonObj = new JSONObject(response.getBody());
				return jsonObj.get("status").toString();
			}
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/retrieve/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Retrieve Content", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Retrieve Content", response = DownloadUrlResponse.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> retrieveContent(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("id") String id) throws CustomException, JSONException {
		String storageLocation_id = null;
		logger.debug("Entering retrieveContent()");
		ResponseEntity<String> response;
		Content content = contentService.findById(id, tenantId);
		if (content != null) {
			ContentLocation contentlocation = contentLocationService.findById(content.getContentLocationId(), tenantId);
			if (contentlocation != null) {
				storageLocation_id = contentlocation.getLocationId();
			}
		} else
			throwContentNotFoundException();
		response = wrapperService.retrieveContent(storageLocation_id, tenantId);
		logger.debug("Exit retrieveContent()");
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Fetching Content Model", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retrieve Content", response = Content.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Content> fetchContentModel(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("id") String id) throws CustomException, JSONException {
		logger.debug("Entering fetchContentModel()");
		Content content = null;
		content = wrapperService.fetchContentModel(id, tenantId);
		logger.debug("Exit fetchContentModel()");
		if (content == null)
			throwContentNotFoundException();
		return new ResponseEntity<Content>(content, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/metadata/{contentId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Content Metadata by Id", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get Content Metadata"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> fetchContentMetadata(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("contentId") String contentId) throws CustomException, JSONException {
		logger.debug("Entering fetchContentMetadata()");
		Content content = null;
		content = wrapperService.fetchContentModel(contentId, tenantId);
		if (content == null)
			throwContentNotFoundException();
		if (content.getMetadata() == null)
			throwContentMetadataNotFoundException();
		JSONObject content_metadata = new JSONObject(content.getMetadata());
		return new ResponseEntity<>(content_metadata.toString(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/metadata/{contentId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete Content Metadata by Id", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Content Metadata deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<Content> deleteContentMetadata(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("contentId") String contentId) throws CustomException, JSONException {
		logger.debug("Entering deleteContentMetadata()");
		Content content = null;
		content = contentService.update(contentId, null, null, tenantId);
		logger.debug("Exit deleteContentMetadata()");
		if (content == null)
			throwContentNotFoundException();
		return new ResponseEntity<>(content, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/metadata", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Search Content on Metadata", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Contents found", response = Content.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<List<Content>> searchContentByMetadata(@RequestHeader(value = "tenantId") String tenantId,
			@RequestBody String searchParams) throws CustomException, JSONException {
		logger.debug("Entering searchContentByMetadata()");
		List<Content> contentList = contentService.searchMetadata(searchParams, tenantId);
		logger.debug("Exit searchContentByMetadata()");
		if (contentList.isEmpty())
			return new ResponseEntity<>(contentList, HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(contentList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/dataclass/{contentId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Content DataClass by Id", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get Content DataClass"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> fetchContentDataClass(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("contentId") String contentId) throws CustomException, JSONException {
		logger.debug("Entering fetchContentDataClass()");
		Content content = null;
		content = wrapperService.fetchContentModel(contentId, tenantId);
		if (content == null)
			throwContentNotFoundException();
		if (content.getDataclass() == null)
			throwContentDataClassNotFoundException();
		JSONObject content_dataclass = new JSONObject(content.getDataclass());
		return new ResponseEntity<>(content_dataclass.toString(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/dataclass/{contentId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete Content Dataclass by Id", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Content Dataclass deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<Content> deleteContentDataClass(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("contentId") String contentId) throws CustomException, JSONException {
		logger.debug("Entering deleteContentDataClass()");
		Content content = null;
		JSONObject delete_json = new JSONObject();
		delete_json.put("removeDataclass", true);
		content = contentService.update(contentId, delete_json.toString(), null, tenantId);
		logger.debug("Exit deleteContentDataClass()");
		if (content == null)
			throwContentNotFoundException();
		return new ResponseEntity<>(content, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/dataclass", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Search Content on Dataclass", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Contents found", response = Content.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<List<Content>> searchContentByDataClass(@RequestHeader(value = "tenantId") String tenantId,
			@RequestBody String searchParams) throws CustomException, JSONException {
		logger.debug("Entering searchContentByDataClass()");
		List<Content> contentList = contentService.searchDataclass(searchParams, tenantId);
		logger.debug("Exit searchContentByDataClass()");
		if (contentList.isEmpty())
			return new ResponseEntity<>(contentList, HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(contentList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/rootpathhierarchy/{contentId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Content hierarchy by Id", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get  Content hierarchy"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> getContentHirearchy(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("contentId") String contentId) throws CustomException, JSONException {
		logger.debug("Entering getContentHirearchy()");
		JSONObject hierarchy_json = new JSONObject();
		// ResponseEntity<String> response = null;
		Content content = wrapperService.fetchContentModel(contentId, tenantId);
		if (content != null) {
			// JSONObject content_json = new JSONObject(response.getBody());
			hierarchy_json.put("content", new JSONObject(content));
			String par_folder = content.getParentFolderId();
			JSONObject folder_hierarchy_json = wrapperService.getFolderHierarchy(par_folder, tenantId);
			if (folder_hierarchy_json.has("folder") && folder_hierarchy_json.has("cabinet")) {
				// JSONObject major_json = new JSONObject(response.getBody());
				// logger.debug(folder_hierarchy_json);
				hierarchy_json.put("folder", folder_hierarchy_json.get("folder"));
				hierarchy_json.put("cabinet", folder_hierarchy_json.get("cabinet"));
			}
		} else
			throwContentNotFoundException();
		logger.debug("Exiting getContentHirearchy()");
		return new ResponseEntity<>(hierarchy_json.toString(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/checkout/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Lock content", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Content checked out", response = Content.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Content> checkOutContent(@PathVariable("id") String id,
			@RequestHeader(value = "tenantId", required = true) String tenantId,
			@RequestHeader(value = "userId", required = true) String userId)
			throws CustomException, JsonProcessingException, InterruptedException, JSONException {
		logger.debug("Entering checkOutContent()");
//		JSONObject updateContentParams = new JSONObject();
//		updateContentParams.put(Content.CHECKEDOUT_PARAM, true);
//		updateContentParams.put("tenantId", tenantId);
//		updateContentParams.put(Content.CHECKEDOUTBY_PARAM, userId);

		Content content = wrapperService.checkOutContent(id, tenantId, userId);
		logger.debug("Exiting checkOutContent()");
		return new ResponseEntity<>(content, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/undocheckout/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Lock content", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Content undo checked out", response = Content.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Content> undoCheckOutContent(@PathVariable("id") String id,
			@RequestHeader(value = "tenantId", required = true) String tenantId,
			@RequestHeader(value = "userId", required = true) String userId)
			throws CustomException, JsonProcessingException, InterruptedException, JSONException {
		logger.debug("Entering undoCheckOutContent()");
//
//		JSONObject updateContentParams = new JSONObject();
//		updateContentParams.put(Content.CHECKEDOUT_PARAM, false);
//		updateContentParams.put("tenantId", tenantId);
//		updateContentParams.put(Content.CHECKEDOUTBY_PARAM, userId);

		Content content = wrapperService.undoCheckOutContent(id, tenantId, userId);
		logger.debug("Exiting checkOutContent()");
		return new ResponseEntity<>(content, HttpStatus.OK);
	}

	@RequestMapping(value = "/checkIn/sync", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "checkIn Content Sync", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Content created", response = Content.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Content> checkInContentSync(@RequestParam("file") MultipartFile file,
			@RequestParam("name") String name, @RequestParam("contentType") String contentType,
			@RequestParam(value = "comments", required = false) String comments,
			@RequestParam(value = "primaryContentId") String primaryContentId,
			// @RequestParam(value = "ownerName") String ownerName,
			// @RequestParam(value = "ownerId") String ownerId,
			@RequestParam(value = "noOfPages") String noOfPages,
			@RequestParam(value = "documentSize") String documentSize,
			@RequestParam(value = "documentType") String documentType,
			@RequestParam(value = "storageCredentialId") String storageCredentialId,
			@RequestParam(value = "metadata", required = false) String metadata,
			@RequestParam(value = "dataclass", required = false) String dataclass,
			@RequestHeader(value = "tenantId") String tenantId,
			@RequestHeader(value = "userId", required = true) String userId,
			@RequestParam(value = "versioningType", required = true) String versioningType)
			throws CustomException, InterruptedException, JSONException, IOException {
		logger.debug("Entering checkInContentSync()");
		ResponseEntity<String> response = null;
		Content content = null;

		// Check if the file is empty or not
		if (file.isEmpty()) {
			throwFileEmptyException();
		} else {
			try {

				String org_file_name = file.getOriginalFilename();
				String unique_file_name = null;
				String guid1 = UUID.randomUUID().toString();

				if (org_file_name.contains(".")) {
					String base_name = org_file_name.substring(0, org_file_name.indexOf("."));
					String ext = org_file_name.substring(org_file_name.indexOf("."), org_file_name.length());
					unique_file_name = base_name + guid1 + ext;
				} else {
					unique_file_name = org_file_name + guid1;
				}

				if (!(versioningType.equalsIgnoreCase(Versioning.MAJOR.name())
						|| versioningType.equalsIgnoreCase(Versioning.MINOR.name()))) {
					throwContentVersioningNotValidException();
				}

				// Get the file and save it in temp folder
				byte[] bytes = file.getBytes();

				String uploadPath = UPLOADED_FOLDER + unique_file_name;
				Path path = Paths.get(uploadPath);
				Files.write(path, bytes);

				response = wrapperService.uploadStoreContent(file, name, contentType, comments, null, null, null,
						storageCredentialId, uploadPath, false, tenantId);

				logger.debug("response.statusCodeUploadStoreContentSync=>" + response.getStatusCode());
				if (response.getStatusCode() == HttpStatus.CREATED) {

					logger.debug("response.statusCodeUploadMetaContent=>" + response.getStatusCode());
					JSONObject jsonObj = new JSONObject(response.getBody());
					String token = jsonObj.get("token").toString();
					String locationId_temp = jsonObj.get("storagelocationid").toString();

					logger.debug("[Token " + token + "] Content uploaded to cloud, about to create content metadata");

					Content latestContent = wrapperService.fetchLatestContentModel(primaryContentId.trim(),
							tenantId.trim());
					logger.debug("latestContent:" + latestContent);
					if (!latestContent.getCheckedOut()) {
						throwContentNotCheckedOutException();
					}
					if (!userId.equals(latestContent.getCheckedOutBy())) {
						throwContentNotCheckedOutByParameterUserException();
					}

					BigDecimal version = latestContent.getVersion();
					if (versioningType.equalsIgnoreCase(Versioning.MAJOR.name())) {
						version = version.add(BigDecimal.valueOf(1.0));
					} else if (versioningType.equalsIgnoreCase(Versioning.MINOR.name())) {
						version = version.add(BigDecimal.valueOf(0.1));
					} else {
						throwContentVersioningNotValidException();
					}

					content = wrapperService.checkInMetaContent(file, name, contentType, comments,
							latestContent.getParentFolderId(), latestContent.getOwnerName(), latestContent.getOwnerId(),
							storageCredentialId, null, noOfPages, documentSize, documentType, metadata,
							latestContent.getPrivilege(), false, locationId_temp, tenantId.trim(), false,
							primaryContentId.trim(), latestContent.getCreationDateTime(), latestContent.getVersion(),
							version, userId, dataclass);
					// Send the response as an output
					if (content != null) {
						logger.debug("[Token " + token + "] Content metadata created " + response.getBody());
						content.setToken("");
						if (EASYSEARCH_ENABLED) {
							// Content upload to Azure search engine!!!
							easySearch.postSearchData(content.getId(), name, contentType, comments,
									latestContent.getParentFolderId(), latestContent.getOwnerName(),
									latestContent.getOwnerId(), noOfPages, documentSize, documentType, tenantId.trim(),
									null);
							// End Content upload to Azure search engine!!!
						}
						// return new ResponseEntity<>(responseJsonObject.toString(),
						// response.getStatusCode());
						Content content1 = wrapperService.undoCheckOutAfterCheckIn(latestContent, tenantId, userId);
						if (content1 == null) {

						}
					}

				} else {
					throwContentUploadFailed();
				}
			} catch (IOException e) {
				logger.debug(e.getMessage(), e);
				e.printStackTrace();
				throwFileStoreException();
			}
			logger.debug("Exit uploadContentSync()");
		}
		if (response == null && content == null) {
			throwContentUploadFailed();
		}
		return new ResponseEntity<>(content, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/setlatest/{id}/{version}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Set Content Version as Latest", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Content Version set as Latest", response = Content.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Content> setContentVersionLatest(@PathVariable("id") String id,
			@PathVariable("version") BigDecimal version,
			@RequestHeader(value = "tenantId", required = true) String tenantId,
			@RequestHeader(value = "userId", required = true) String userId)
			throws CustomException, JsonProcessingException, InterruptedException, JSONException {

		logger.debug("Entering setContentVersionLatest()");

		Content content = null;

		content = wrapperService.setContentVersionLatest(id, version, tenantId);

		logger.debug("Exiting setContentVersionLatest()");
		return new ResponseEntity<>(content, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/search/fts", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Full Text Search", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Contents found", response = Content.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<SearchResults> fullTextSearch(@RequestHeader(value = "tenantId") String tenantId,
			@RequestBody FTSSearchDTO ftsSearchDTO)
			throws CustomException, JSONException, JsonProcessingException, ParseException {
		logger.debug("Entering fullTextSearch()");
		SearchResults searchResults = contentService.fullTextSearch(ftsSearchDTO.getParams(), tenantId);
		logger.debug("Exit fullTextSearch()");
		/*
		 * if(contentList.isEmpty()) return new ResponseEntity<>(contentList,
		 * HttpStatus.NOT_FOUND);
		 */
		return new ResponseEntity<>(searchResults, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getLastModifiedContent/{parentFolderId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Sorting Folders based on revised date time", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get content", response = Content.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Content> getLastModifiedFolder(HttpServletRequest request,
			@PathVariable("parentFolderId") String parentFolderId, @RequestHeader(value = "tenantId") String tenantId)
			throws CustomException, JSONException, IOException {
		logger.debug("Entering getLastModifiedFolderForParentFolderId()");
		if (request.getHeader("tenantId") == null) {
			throwInvalidTenantException();
		}

		Content content = contentService.getLastModifiedContent(parentFolderId, tenantId);

		logger.debug("Exit getLastModifiedFolderForParentFolderId()");
		return new ResponseEntity<Content>(content, HttpStatus.OK);
	}
}
