package com.newgen.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.dto.UserGroupAssociationDTO;
import com.newgen.model.UserGroupAssociation;
import com.newgen.model.ValidationError;
import com.newgen.validation.ValidationErrorBuilder;
import com.newgen.wrapper.service.WrapperService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/userrights")
@Api(value = "User Group Association Registration", description = "Operations for User Profile Registration")
public class HybridRoleRightUserGroupAssociationController {
	private static final Logger logger = LoggerFactory.getLogger(HybridRoleRightUserGroupAssociationController.class);

	@Autowired
	private WrapperService wrapperService;

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ValidationError handleException(MethodArgumentNotValidException exception) {
		return createValidationError(exception);
	}

	private ValidationError createValidationError(MethodArgumentNotValidException exception) {
		return ValidationErrorBuilder.fromBindingErrors(exception.getBindingResult());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/objectId={id}/userId={userId}", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "Registered secured data for of a group")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Registered User Profile", response = UserGroupAssociation.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public String getUserRightsById(@RequestHeader(value = "tenantId") String tenantId, @PathVariable("id") String id,
			@PathVariable("userId") String userId, HttpServletRequest request) {
		logger.debug("Entering getUserGroupAssociationById()");
		String authToken = request.getHeader("authToken").toString();
		String str = wrapperService.readUserRightsById(id, userId, tenantId, authToken);
		if (str == null) {
			// return new ResponseEntity<UserProfile>(userGroupAssociation,
			// HttpStatus.NOT_FOUND);
		}
		logger.debug("Exit getUserGroupAssociationById()");
		// return new ResponseEntity<UserProfile>(userGroupAssociation,
		// HttpStatus.FOUND);
		return str;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a User Profile", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "User Profile deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<UserGroupAssociation> deleteUserGroupAssociation(
			@RequestHeader(value = "tenantId") String tenantId, @PathVariable("id") String id) throws JSONException {
		logger.debug("Entering deleteUserGroupAssociation()");
		UserGroupAssociation userGroupAssociation = wrapperService.deleteUserGroupAssociation(id, tenantId);
		System.out.println(userGroupAssociation);
		logger.debug("Exit deleteUserGroupAssociation()");
		if (userGroupAssociation == null) {
			return new ResponseEntity<UserGroupAssociation>(userGroupAssociation, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<UserGroupAssociation>(userGroupAssociation, HttpStatus.NO_CONTENT);
	}

	@ApiOperation(value = "Update User Profile")
	@RequestMapping(method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "User Profile updated", response = UserGroupAssociation.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<UserGroupAssociation> updateUserGroupAssociation(
			@RequestHeader(value = "tenantId") String tenantId,
			@RequestBody UserGroupAssociationDTO userGroupAssociationDTO)
			throws JSONException, JsonParseException, JsonMappingException, IOException {
		logger.debug("Entering updateUserGroupAssociation()");

		userGroupAssociationDTO.setTenantId(tenantId);
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> updateParams = mapper.convertValue(userGroupAssociationDTO, Map.class);
		// .writeValueAsString(secureDataRegistrationDTO);

		UserGroupAssociation userGroupAssociation = wrapperService.updateUserGroupAssociation(updateParams,
				userGroupAssociationDTO.getGroupId(), tenantId);
		System.out.println("updateUserGroupAssociation" + userGroupAssociation);
		logger.debug("Exit updateUserGroupAssociation()");
		return new ResponseEntity<UserGroupAssociation>(userGroupAssociation, HttpStatus.OK);
	}

}
