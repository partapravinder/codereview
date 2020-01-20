package com.newgen.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.dto.BulkGroupProfileDTO;
import com.newgen.dto.BulkUserProfileDTO;
import com.newgen.dto.GroupProfileDTO;
import com.newgen.dto.UserGroupAssociationDTO;
import com.newgen.dto.UserProfileDTO;
import com.newgen.exception.CustomException;
import com.newgen.model.GroupProfile;
import com.newgen.model.GroupUserAssociation;
import com.newgen.model.UserGroupAssociation;
import com.newgen.model.UserProfile;
import com.newgen.model.ValidationError;
import com.newgen.validation.ValidationErrorBuilder;
import com.newgen.wrapper.service.WrapperService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "User/Group Profile Registration", description = "Operations for User/Group Profile Registration")
public class HybridRoleRightUserGroupProfileController extends ExceptionThrower {
	private static final Logger logger = LoggerFactory.getLogger(HybridRoleRightUserGroupProfileController.class);

	List<UserProfile> bulkResponse1 = null;
	List<GroupProfile> bulkResponse2 = null;

	@Autowired
	ExceptionThrower exceptionThrower;

	@Autowired
	private WrapperService wrapperService;

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, value = "/userprofile")
	@ApiOperation(value = "Register User Profile", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "User Profile Registered", response = UserProfile.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<UserProfile> register(@Valid @RequestHeader(value = "tenantId") String tenantId,
			@Valid @RequestBody UserProfileDTO userProfileDTO) throws JSONException, CustomException {
		logger.debug("Entering register()");
		// String parentFolderId = "";
		// TODO get parentFolderId of the folderId or contentId from content and folder
		// service and set in each profile.
		userProfileDTO.setTenantId(tenantId);
		UserProfile userProfile = new UserProfile(tenantId, userProfileDTO.getUserId(), userProfileDTO.getProfiles());

		if (userProfileDTO.getGroupIds() != null) {
			userProfile.setGroupIds(userProfileDTO.getGroupIds());
		}

		UserProfile response = wrapperService.registerUser(userProfile);
		logger.debug("Exit registerSecuredData()");
		System.out.println(response.toString());
		return new ResponseEntity<UserProfile>(response, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, value = "/userprofile/save")
	@ApiOperation(value = "Register Group Profile", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Group Profile Registered", response = GroupProfile.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<UserProfile> saveUser(@Valid @RequestHeader(value = "tenantId") String tenantId,
			@Valid @RequestBody UserProfileDTO userProfileDTO) throws JSONException, IOException, CustomException {
		logger.debug("Entering save()");
		// String parentFolderId = "";
		// TODO get parentFolderId of the folderId or contentId from content and folder
		// service and set in each profile.
		userProfileDTO.setTenantId(tenantId);
		UserProfile response = null;
		if (userProfileDTO.getUserId() != null) {
			UserProfile userProfile1 = wrapperService.readUserProfileById(userProfileDTO.getUserId(), tenantId);
			if (userProfile1 != null) {
				JSONObject json = new JSONObject(userProfileDTO);
				response = wrapperService.updateUserProfile(userProfileDTO, json.toString(), tenantId);
			} else {
				UserProfile userProfile = new UserProfile(tenantId, userProfileDTO.getUserId(),
						userProfileDTO.getProfiles());

				if (userProfileDTO.getGroupIds() != null) {
					userProfile.setGroupIds(userProfileDTO.getGroupIds());
				}
				response = wrapperService.registerUser(userProfile);
			}
		} else {
			/*
			 * UserProfile userProfile = new UserProfile(tenantId,
			 * userProfileDTO.getUserId(), userProfileDTO.getProfile());
			 * 
			 * if (userProfileDTO.getGroupIds() != null) {
			 * userProfile.setGroupIds(userProfileDTO.getGroupIds()); } response =
			 * wrapperService.registerUser(userProfile);
			 */
			exceptionThrower.throwNoUserIdNotPresentInRequest();
		}
		logger.debug("Exit registerSecuredData()");
		System.out.println(response.toString());
		return new ResponseEntity<UserProfile>(response, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, value = "/userprofile/save/bulk")
	@ApiOperation(value = "Register Bulk User Profile", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "User Profile Registered", response = UserProfile.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<UserProfile>> bulkRegister(@Valid @RequestHeader(value = "tenantId") String tenantId,
			@Valid @RequestBody BulkUserProfileDTO bulkUserProfileDTO) throws JSONException, CustomException {
		logger.debug("Entering bulkRegister()");

		bulkUserProfileDTO.setTenantId(tenantId);
		bulkResponse1 = new ArrayList<UserProfile>();

		if (!bulkUserProfileDTO.getUserIdList().isEmpty()) {
			bulkUserProfileDTO.getUserIdList().stream().forEach(userId -> {
				// String parentFolderId = "";
				UserProfile userProfile1 = wrapperService.readUserProfileById(userId, tenantId);
				if (userProfile1 != null) {
					UserProfileDTO userProfileDTO = new UserProfileDTO(tenantId, userId,
							bulkUserProfileDTO.getProfiles(), bulkUserProfileDTO.getGroupIds());
					JSONObject json = new JSONObject(userProfileDTO);
					try {
						userProfile1 = wrapperService.updateUserProfile(userProfileDTO, json.toString(), tenantId);
						// response.add(userProfile1);
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CustomException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					userProfile1 = new UserProfile(tenantId, userId, bulkUserProfileDTO.getProfiles(),
							bulkUserProfileDTO.getGroupIds());

					try {
						wrapperService.registerUser(userProfile1);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CustomException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				bulkResponse1.add(userProfile1);

				// response.add(userProfile1);
				// TODO get parentFolderId of the folderId or contentId from content and folder
				// service and set in each profile.
				/*
				 * UserProfile userProfile = new UserProfile(tenantId, userId,
				 * bulkUserProfileDTO.getProfiles(), bulkUserProfileDTO.getGroupIds());
				 * listUserProfile.add(userProfile);
				 */
			});

			// response = wrapperService.registerBulkUsers(listUserProfile, tenantId);
			logger.debug("Exit registerSecuredData()");
			System.out.println(bulkResponse1.toString());
		}
		return new ResponseEntity<List<UserProfile>>(bulkResponse1, HttpStatus.CREATED);
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ValidationError handleException(MethodArgumentNotValidException exception) {
		return createValidationError(exception);
	}

	private ValidationError createValidationError(MethodArgumentNotValidException exception) {
		return ValidationErrorBuilder.fromBindingErrors(exception.getBindingResult());
	}

	@RequestMapping(method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE, value = "/userprofile/favourites")
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "Registered secured data for of a group")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User Profile", response = UserProfile.class),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<UserProfile>> searchFavouritesById(@RequestHeader(value = "tenantId") String tenantId,
			HttpServletRequest request) {
		logger.debug("Entering getUserProfileByGroup()");
		List<UserProfile> userProfileList = wrapperService.searchFavourites(request.getParameterMap(), tenantId);
		if (userProfileList.size() < 1) {
			return new ResponseEntity<List<UserProfile>>(userProfileList, HttpStatus.NOT_FOUND);
		}
		logger.debug("Exit getUserProfileByGroup()");
		return new ResponseEntity<List<UserProfile>>(userProfileList, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/userprofile/{id}", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "Registered secured data for of a group")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Registered User Profile", response = UserProfile.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<UserProfile> getUserProfileById(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("id") String id) {
		logger.debug("Entering getUserProfileById()");
		UserProfile userProfile = wrapperService.readUserProfileById(id, tenantId);
		if (userProfile == null) {
			return new ResponseEntity<UserProfile>(userProfile, HttpStatus.NOT_FOUND);
		}
		logger.debug("Exit getUserProfileById()");
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/userprofile/favourites/{id}", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "Registered secured data for of a group")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Registered User Profile", response = UserProfile.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<UserProfile> getUserProfileFavouritesById(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("id") String id) {
		logger.debug("Entering getUserProfileById()");
		UserProfile userProfile = wrapperService.readUserProfileFavouritesById(id, tenantId);
		if (userProfile == null) {
			return new ResponseEntity<UserProfile>(userProfile, HttpStatus.NOT_FOUND);
		}
		logger.debug("Exit getUserProfileById()");
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE, value = "/userprofile")
	@ApiOperation(value = "Searching for User Profile", notes = "Other fields may be included to further filter the list returned.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "User Profile list", response = UserProfile.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<UserProfile>> searchUserProfile(HttpServletRequest request) throws CustomException {
		logger.debug("Entering searchUserProfile()");
		String tenantId = request.getHeader("tenantId").toString();
		Map<String, String[]> allRequestParams = request.getParameterMap();

		System.out.println("allRequestParams------------------->" + request.toString());
		List<UserProfile> userProfile = wrapperService.searchUsers(allRequestParams, tenantId);
		logger.debug("Exiting search User Profile()");
		if (userProfile.isEmpty())
			return new ResponseEntity<List<UserProfile>>(userProfile, HttpStatus.OK);
		return new ResponseEntity<List<UserProfile>>(userProfile, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/userprofile/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a User Profile", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "User Profile deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<UserProfile> deleteUserProfile(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("id") String id) throws JSONException {
		logger.debug("Entering deleteUserProfile()");
		UserProfile userProfile = wrapperService.deleteUserProfile(id, tenantId);
		System.out.println(userProfile);
		logger.debug("Exit deleteUserProfile()");
		if (userProfile == null) {
			return new ResponseEntity<UserProfile>(userProfile, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.NO_CONTENT);
	}

	@ApiOperation(value = "Update User Profile")
	@RequestMapping(method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE, value = "/userprofile")
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User Profile updated", response = UserProfile.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<UserProfile> updateUserProfile(@RequestHeader(value = "tenantId") String tenantId,
			@RequestBody String updateParams)
			throws JSONException, JsonParseException, JsonMappingException, IOException, CustomException {
		logger.debug("Entering updateUserProfile()");

		// userProfileDTO.setTenantId(tenantId);
		// ObjectMapper mapper = new ObjectMapper();
		// @SuppressWarnings("unchecked")
		// Map<String, Object> updateParams = mapper.convertValue(userProfileDTO,
		// Map.class);
		// .writeValueAsString(secureDataRegistrationDTO);

		if (updateParams == null) {
			// Throw Exception - There is nothing to update.
		}

		UserProfile userProfile = wrapperService.updateUserProfile(null, updateParams, tenantId);
		System.out.println("updateUserProfile" + userProfile);
		logger.debug("Exit updateUserProfile()");
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, value = "/groupprofile")
	@ApiOperation(value = "Register Group Profile", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Group Profile Registered", response = GroupProfile.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<GroupProfile> register(@Valid @RequestHeader(value = "tenantId") String tenantId,
			@Valid @RequestBody GroupProfileDTO groupProfileDTO) throws JSONException, IOException, CustomException {
		logger.debug("Entering register()");
		// String parentFolderId = "";
		// TODO get parentFolderId of the folderId or contentId from content and folder
		// service and set in each profile.
		groupProfileDTO.setTenantId(tenantId);
		GroupProfile groupProfile = new GroupProfile(tenantId, groupProfileDTO.getGroupId(),
				groupProfileDTO.getGroupName(), groupProfileDTO.getProfiles());
		if (groupProfileDTO.getRights() != null) {
			groupProfile.setRights(groupProfileDTO.getRights());
		}

		if (groupProfileDTO.getUserIds() != null) {
			groupProfile.setUserIds(groupProfileDTO.getUserIds());
		}
		GroupProfile response = wrapperService.registerGroup(groupProfile);
		logger.debug("Exit registerSecuredData()");
		System.out.println(response.toString());
		return new ResponseEntity<GroupProfile>(response, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, value = "/groupprofile/save")
	@ApiOperation(value = "Register Group Profile", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Group Profile Registered", response = GroupProfile.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<GroupProfile> saveGroup(@Valid @RequestHeader(value = "tenantId") String tenantId,
			@Valid @RequestBody GroupProfileDTO groupProfileDTO) throws JSONException, IOException, CustomException {
		logger.debug("Entering save()");
		// String parentFolderId = "";
		// TODO get parentFolderId of the folderId or contentId from content and folder
		// service and set in each profile.
		groupProfileDTO.setTenantId(tenantId);
		GroupProfile response;
		if (groupProfileDTO.getGroupId() != null) {
			GroupProfile groupProfile1 = wrapperService.readGroupProfileById(groupProfileDTO.getGroupId(), tenantId);
			if (groupProfile1 != null) {
				// JSONObject json = new JSONObject(groupProfileDTO);
				response = wrapperService.updateGroupProfile(groupProfileDTO, null, tenantId);
			} else {
				GroupProfile groupProfile = new GroupProfile(tenantId, groupProfileDTO.getGroupId(),
						groupProfileDTO.getGroupName(), groupProfileDTO.getProfiles());
				if (groupProfileDTO.getRights() != null) {
					groupProfile.setRights(groupProfileDTO.getRights());
				}

				if (groupProfileDTO.getUserIds() != null) {
					groupProfile.setUserIds(groupProfileDTO.getUserIds());
				}
				response = wrapperService.registerGroup(groupProfile);
			}
		} else {
			GroupProfile groupProfile = new GroupProfile(tenantId, groupProfileDTO.getGroupId(),
					groupProfileDTO.getGroupName(), groupProfileDTO.getProfiles());
			if (groupProfileDTO.getRights() != null) {
				groupProfile.setRights(groupProfileDTO.getRights());
			}

			if (groupProfileDTO.getUserIds() != null) {
				groupProfile.setUserIds(groupProfileDTO.getUserIds());
			}
			response = wrapperService.registerGroup(groupProfile);
		}
		logger.debug("Exit registerSecuredData()");
		System.out.println(response.toString());
		return new ResponseEntity<GroupProfile>(response, HttpStatus.CREATED);
	}

	/*
	 * @RequestMapping(method = RequestMethod.POST, produces =
	 * APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, value =
	 * "/groupprofile/bulk")
	 * 
	 * @ApiOperation(value = "Register Bulk Group Profile", produces =
	 * APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	 * 
	 * @ResponseStatus(code = HttpStatus.CREATED)
	 * 
	 * @ApiResponses(value = {
	 * 
	 * @ApiResponse(code = 201, message = "Group Profile Registered", response =
	 * GroupProfile.class),
	 * 
	 * @ApiResponse(code = 500, message = "Internal Server Error"),
	 * 
	 * @ApiResponse(code = 503, message = "Database connection failure"),
	 * 
	 * @ApiResponse(code = 400, message = "Exception Message") }) public
	 * ResponseEntity<List<GroupProfile>> bulkRegister(@Valid @RequestHeader(value =
	 * "tenantId") String tenantId,
	 * 
	 * @RequestBody BulkGroupProfileDTO bulkGroupProfileDTO) throws JSONException {
	 * logger.debug("Entering bulkRegister()");
	 * bulkGroupProfileDTO.setTenantId(tenantId); List<GroupProfile>
	 * listGroupProfile = new ArrayList<>();
	 * 
	 * bulkGroupProfileDTO.getGroupIdList().stream().forEach(group -> { // String
	 * parentFolderId = ""; // TODO get parentFolderId of the folderId or contentId
	 * from content and folder // service and set in each profile. GroupProfile
	 * groupProfile = new GroupProfile(tenantId, group.getGroupId(),
	 * group.getGroupName(), group.getProfiles()); if (group.getRights() != null) {
	 * groupProfile.setRights(group.getRights()); }
	 * 
	 * if (group.getUserIds() != null) {
	 * groupProfile.setUserIds(group.getUserIds()); }
	 * listGroupProfile.add(groupProfile); });
	 * 
	 * List<GroupProfile> response =
	 * wrapperService.registerBulkGroups(listGroupProfile, tenantId);
	 * logger.debug("Exit registerSecuredData()");
	 * System.out.println(response.toString()); return new
	 * ResponseEntity<List<GroupProfile>>(response, HttpStatus.CREATED); }
	 */

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, value = "/groupprofile/save/bulk")
	@ApiOperation(value = "Register Bulk User Profile", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "User Profile Registered", response = UserProfile.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<GroupProfile>> bulkRegisterGroups(
			@Valid @RequestHeader(value = "tenantId") String tenantId,
			@Valid @RequestBody BulkGroupProfileDTO bulkGroupProfileDTO) throws JSONException, CustomException {
		logger.debug("Entering bulkRegister()");

		bulkGroupProfileDTO.setTenantId(tenantId);
		bulkResponse2 = new ArrayList<GroupProfile>();

		if (!bulkGroupProfileDTO.getGroupIdList().isEmpty()) {
			bulkGroupProfileDTO.getGroupIdList().stream().forEach(groupId -> {
				// String parentFolderId = "";
				GroupProfile groupProfile1 = wrapperService.readGroupProfileById(groupId, tenantId);
				if (groupProfile1 != null) {
					GroupProfileDTO groupProfileDTO = new GroupProfileDTO(tenantId, groupId,
							groupProfile1.getGroupName(), bulkGroupProfileDTO.getUserIds(),
							bulkGroupProfileDTO.getProfiles(), bulkGroupProfileDTO.getRights());
					JSONObject json = new JSONObject(groupProfileDTO);
					try {
						groupProfile1 = wrapperService.updateGroupProfile(groupProfileDTO, json.toString(), tenantId);
						// response.add(userProfile1);
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CustomException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				bulkResponse2.add(groupProfile1);
			});
		}
		return new ResponseEntity<List<GroupProfile>>(bulkResponse2, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/groupprofile/{id}", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "Registered secured data for of a group")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Registered Group Profile", response = GroupProfile.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<GroupProfile> getGroupProfileById(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("id") String id) {
		logger.debug("Entering getGroupProfileById()");
		GroupProfile groupProfile = wrapperService.readGroupProfileById(id, tenantId);
		if (groupProfile == null) {
			return new ResponseEntity<GroupProfile>(groupProfile, HttpStatus.NOT_FOUND);
		}
		logger.debug("Exit getGroupProfileById()");
		return new ResponseEntity<GroupProfile>(groupProfile, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE, value = "/groupprofile")
	@ApiOperation(value = "Searching for Group Profile", notes = "Other fields may be included to further filter the list returned.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Group Profile list", response = GroupProfile.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<GroupProfile>> searchGroupProfile(HttpServletRequest request) throws CustomException {
		logger.debug("Entering searchGroupProfile()");
		String tenantId = request.getHeader("tenantId").toString();
		Map<String, String[]> allRequestParams = request.getParameterMap();
		List<GroupProfile> groupProfile = wrapperService.searchGroups(allRequestParams, tenantId);
		logger.debug("Exiting search Group Profile()");
		if (groupProfile.isEmpty())
			return new ResponseEntity<List<GroupProfile>>(groupProfile, HttpStatus.OK);
		return new ResponseEntity<List<GroupProfile>>(groupProfile, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/groupprofile/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a Group Profile", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Group Profile deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<GroupProfile> deleteGroupProfile(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("id") String id) throws JSONException {
		logger.debug("Entering deleteGroupProfile()");
		GroupProfile groupProfile = wrapperService.deleteGroupProfile(id, tenantId);
		System.out.println(groupProfile);
		logger.debug("Exit deleteGroupProfile()");
		if (groupProfile == null) {
			return new ResponseEntity<GroupProfile>(groupProfile, HttpStatus.NOT_FOUND);
		}
		// return new ResponseEntity<GroupProfile>(response.getBody(),
		// HttpStatus.NO_CONTENT);
		return new ResponseEntity<GroupProfile>(groupProfile, HttpStatus.NO_CONTENT);
	}

	@ApiOperation(value = "Update Group Profile")
	@RequestMapping(method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE, value = "/groupprofile")
	@ResponseStatus(code = HttpStatus.OK)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Group Profile updated", response = GroupProfile.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<GroupProfile> updateGroupProfile(@RequestHeader(value = "tenantId") String tenantId,
			@RequestBody String updateParams)
			throws JSONException, JsonParseException, JsonMappingException, IOException, CustomException {
		logger.debug("Entering updateGroupProfile()");

		if (updateParams == null) {
			// Throw Exception - There is nothing to update.
		}

		// ObjectMapper mapper = new ObjectMapper();
		// @SuppressWarnings("unchecked")
		// Map<String, Object> updateParamsMap = mapper.readValue(updateParams,
		// Map.class);
		// System.out.println(updateParamsMap.get("groupId"));
		// writeValueAsString(secureDataRegistrationDTO);

		GroupProfileDTO groupProfileDTO = new GroupProfileDTO();
		GroupProfile groupProfile = wrapperService.updateGroupProfile(groupProfileDTO, updateParams, tenantId);
		if (groupProfile == null) {
			return new ResponseEntity<GroupProfile>(groupProfile, HttpStatus.NOT_FOUND);
		}
		System.out.println("updateGroupProfile-" + groupProfile);
		logger.debug("Exit updateGroupProfile()");
		return new ResponseEntity<GroupProfile>(groupProfile, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, value = "/usergroupassociation")
	@ApiOperation(value = "Register User/Group Association", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "User/Group Association Registered", response = UserGroupAssociation.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<UserGroupAssociation> register(@Valid @RequestHeader(value = "tenantId") String tenantId,
			@Valid @RequestBody UserGroupAssociationDTO userGroupAssociationDTO) throws JSONException {
		logger.debug("Entering register()");
		// String parentFolderId = "";
		// TODO get parentFolderId of the folderId or contentId from content and folder
		// service and set in each profile.
		userGroupAssociationDTO.setTenantId(tenantId);
		UserGroupAssociation userGroupAssociation = new UserGroupAssociation(tenantId,
				userGroupAssociationDTO.getGroupId(), userGroupAssociationDTO.getUserIds());

		UserGroupAssociation response = wrapperService.register(userGroupAssociation);
		logger.debug("Exit registerSecuredData()");
		System.out.println(response.toString());
		return new ResponseEntity<UserGroupAssociation>(response, HttpStatus.CREATED);
	}

	/*
	 * @RequestMapping(method = RequestMethod.POST, produces =
	 * APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, value = "/bulk")
	 * 
	 * @ApiOperation(value = "Register Bulk User Profile", produces =
	 * APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	 * 
	 * @ResponseStatus(code = HttpStatus.CREATED)
	 * 
	 * @ApiResponses(value = {
	 * 
	 * @ApiResponse(code = 201, message = "User Profile Registered", response =
	 * UserGroupAssociation.class),
	 * 
	 * @ApiResponse(code = 500, message = "Internal Server Error"),
	 * 
	 * @ApiResponse(code = 503, message = "Database connection failure"),
	 * 
	 * @ApiResponse(code = 400, message = "Exception Message") }) public
	 * ResponseEntity<List<UserGroupAssociation>> bulkRegister(
	 * 
	 * @Valid @RequestHeader(value = "tenantId") String tenantId,
	 * 
	 * @Valid @RequestBody BulkUserGroupAssociationDTO bulkUserGroupAssociationDTO)
	 * throws JSONException { logger.debug("Entering bulkRegister()");
	 * bulkUserGroupAssociationDTO.setTenantId(tenantId); List<UserGroupAssociation>
	 * listUserGroupAssociation = new ArrayList<>();
	 * 
	 * bulkUserGroupAssociationDTO.getUserIdList().stream().forEach(userId -> { //
	 * String parentFolderId = ""; // TODO get parentFolderId of the folderId or
	 * contentId from content and folder // service and set in each profile.
	 * UserGroupAssociation userGroupAssociation = new
	 * UserGroupAssociation(tenantId, userId,
	 * bulkUserGroupAssociationDTO.getProfile());
	 * listUserGroupAssociation.add(userGroupAssociation); });
	 * 
	 * List<UserGroupAssociation> response =
	 * wrapperService.registerBulk(listUserGroupAssociation);
	 * logger.debug("Exit registerSecuredData()");
	 * System.out.println(response.toString()); return new
	 * ResponseEntity<List<UserGroupAssociation>>(response, HttpStatus.CREATED); }
	 */

	/*
	 * @RequestMapping(method = RequestMethod.GET, produces =
	 * APPLICATION_JSON_VALUE, value = "/favourites")
	 * 
	 * @ResponseStatus(code = HttpStatus.OK)
	 * 
	 * @ApiOperation(value = "Registered secured data for of a group")
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 200, message = "User Profile",
	 * response = UserGroupAssociation.class),
	 * 
	 * @ApiResponse(code = 503, message = "Database connection failure"),
	 * 
	 * @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 400,
	 * message = "Exception Message") }) public
	 * ResponseEntity<List<UserGroupAssociation>> searchFavouritesById(
	 * 
	 * @RequestHeader(value = "tenantId") String tenantId, HttpServletRequest
	 * request) { logger.debug("Entering getUserGroupAssociationByGroup()");
	 * List<UserGroupAssociation> userGroupAssociationList =
	 * wrapperService.searchFavourites(request.getParameterMap(), tenantId); if
	 * (userGroupAssociationList.size() < 1) { return new
	 * ResponseEntity<List<UserGroupAssociation>>(userGroupAssociationList,
	 * HttpStatus.NOT_FOUND); }
	 * logger.debug("Exit getUserGroupAssociationByGroup()"); return new
	 * ResponseEntity<List<UserGroupAssociation>>(userGroupAssociationList,
	 * HttpStatus.FOUND); }
	 */

	@RequestMapping(method = RequestMethod.GET, value = "/usergroupassociation/{id}", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "Registered secured data for of a group")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Registered User/Group Association", response = UserGroupAssociation.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<UserGroupAssociation> getUserGroupAssociationById(
			@RequestHeader(value = "tenantId") String tenantId, @PathVariable("id") String id) {
		logger.debug("Entering getUserGroupAssociationById()");
		UserGroupAssociation userGroupAssociation = wrapperService.readUserGroupAssociationById(id, tenantId);
		if (userGroupAssociation == null) {
			return new ResponseEntity<UserGroupAssociation>(userGroupAssociation, HttpStatus.NOT_FOUND);
		}
		logger.debug("Exit getUserGroupAssociationById()");
		return new ResponseEntity<UserGroupAssociation>(userGroupAssociation, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/usergroupassociation", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "Registered secured data for of a group")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Registered User/Group Association", response = UserGroupAssociation.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<UserGroupAssociation>> getUserGroupAssociationForTenant(
			@RequestHeader(value = "tenantId") String tenantId) {
		logger.debug("Entering getUserGroupAssociationById()");
		List<UserGroupAssociation> userGroupAssociations = wrapperService.list(tenantId);
		if (userGroupAssociations == null) {
			return new ResponseEntity<List<UserGroupAssociation>>(userGroupAssociations, HttpStatus.NOT_FOUND);
		}
		logger.debug("Exit getUserGroupAssociationById()");
		return new ResponseEntity<List<UserGroupAssociation>>(userGroupAssociations, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/groupuserassociation/{id}", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "Registered secured data for of a group")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Registered User/Group Association", response = GroupUserAssociation.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<GroupUserAssociation> getGroupUserAssociationById(
			@RequestHeader(value = "tenantId") String tenantId, @PathVariable("id") String id) {
		logger.debug("Entering getUserGroupAssociationById()");
		// UserGroupAssociation userGroupAssociation =
		// wrapperService.readUserGroupAssociationById(id, tenantId);
		GroupUserAssociation groupUserAssociation = wrapperService.readGroupUserAssociationById(id, tenantId);
		if (groupUserAssociation == null) {
			return new ResponseEntity<GroupUserAssociation>(groupUserAssociation, HttpStatus.NOT_FOUND);
		}
		logger.debug("Exit getGroupUserAssociationById()");
		return new ResponseEntity<GroupUserAssociation>(groupUserAssociation, HttpStatus.FOUND);
	}

	/*
	 * @RequestMapping(method = RequestMethod.GET, produces =
	 * APPLICATION_JSON_VALUE)
	 * 
	 * @ApiOperation(value = "Searching for User Profile", notes =
	 * "Other fields may be included to further filter the list returned.")
	 * 
	 * @ApiResponses(value = {
	 * 
	 * @ApiResponse(code = 200, message = "User Profile list", response =
	 * UserGroupAssociation.class, responseContainer = "List"),
	 * 
	 * @ApiResponse(code = 503, message = "Database connection failure"),
	 * 
	 * @ApiResponse(code = 400, message = "Exception Message") }) public
	 * ResponseEntity<List<UserGroupAssociation>>
	 * searchUserGroupAssociation(HttpServletRequest request) throws CustomException
	 * { logger.debug("Entering searchUserGroupAssociation()"); String tenantId =
	 * request.getHeader("tenantId").toString(); Map<String, String[]>
	 * allRequestParams = request.getParameterMap(); List<UserGroupAssociation>
	 * userGroupAssociation = wrapperService.search(allRequestParams, tenantId);
	 * logger.debug("Exiting search User Profile()"); if
	 * (userGroupAssociation.isEmpty()) return new
	 * ResponseEntity<List<UserGroupAssociation>>(userGroupAssociation,
	 * HttpStatus.NOT_FOUND); return new
	 * ResponseEntity<List<UserGroupAssociation>>(userGroupAssociation,
	 * HttpStatus.OK); }
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/usergroupassociation/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a User Profile", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "User/Group Association deleted"),
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

	@RequestMapping(method = RequestMethod.PUT, value = "/groupprofile/deallocate/{groupId}/{userId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a User Profile", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "User/Group Association deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<GroupProfile> deallocateUserFromGroup(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("groupId") String groupId, @PathVariable("userId") String userIds[])
			throws JSONException, CustomException {
		System.out.println(userIds.length);
		logger.debug("Entering deleteUserGroupAssociation()");
		GroupProfile groupProfile = wrapperService.deallocateUserFromGroup(tenantId, groupId, userIds);
		System.out.println(groupProfile);
		logger.debug("Exit deleteUserGroupAssociation()");
		if (groupProfile == null) {
			return new ResponseEntity<GroupProfile>(groupProfile, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<GroupProfile>(groupProfile, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/userprofile/deallocate/{userId}/{groupId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a User Profile", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "User/Group Association deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<UserProfile> deallocateGroupFromUser(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("userId") String userId, @PathVariable("groupId") String groupIds[])
			throws JSONException, CustomException {
		System.out.println(groupIds.length);
		logger.debug("Entering deleteUserGroupAssociation()");
		UserProfile userProfile = wrapperService.deallocateGroupFromUser(tenantId, userId, groupIds);
		System.out.println(userProfile);
		logger.debug("Exit deleteUserGroupAssociation()");
		if (userProfile == null) {
			return new ResponseEntity<UserProfile>(userProfile, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/groupprofile/profile/deallocate/{groupId}/{objectId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a User Profile", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "User/Group Association deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<GroupProfile> deallocateProfileFromGroup(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("groupId") String groupId, @PathVariable("objectId") String objectIds[])
			throws JSONException, CustomException {
		System.out.println(objectIds.length);
		logger.debug("Entering deleteUserGroupAssociation()");
		GroupProfile groupProfile = wrapperService.deallocateProfileFromGroup(tenantId, groupId, objectIds);
		System.out.println(groupProfile);
		logger.debug("Exit deleteUserGroupAssociation()");
		if (groupProfile == null) {
			return new ResponseEntity<GroupProfile>(groupProfile, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<GroupProfile>(groupProfile, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/userprofile/profile/deallocate/{userId}/{objectId}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a User Profile", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "User/Group Association deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<UserProfile> deallocateProfileFromUser(@RequestHeader(value = "tenantId") String tenantId,
			@PathVariable("userId") String userId, @PathVariable("objectId") String objectIds[])
			throws JSONException, CustomException {
		System.out.println(objectIds.length);
		logger.debug("Entering deleteUserGroupAssociation()");
		UserProfile userProfile = wrapperService.deallocateProfileFromUser(tenantId, userId, objectIds);
		System.out.println(userProfile);
		logger.debug("Exit deleteUserGroupAssociation()");
		if (userProfile == null) {
			return new ResponseEntity<UserProfile>(userProfile, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.OK);
	}

	/*
	 * @ApiOperation(value = "Update User Profile")
	 * 
	 * @RequestMapping(method = RequestMethod.PUT, produces =
	 * APPLICATION_JSON_VALUE, value = "/usergroupassociation")
	 * 
	 * @ResponseStatus(code = HttpStatus.OK)
	 * 
	 * @ApiResponses(value = {
	 * 
	 * @ApiResponse(code = 200, message = "User/Group Association updated", response
	 * = UserGroupAssociation.class),
	 * 
	 * @ApiResponse(code = 500, message = "Internal Server Error"),
	 * 
	 * @ApiResponse(code = 423, message = "Lock Already exists"),
	 * 
	 * @ApiResponse(code = 503, message = "Database connection failure") }) public
	 * ResponseEntity<GroupProfile> updateGroupProfile1(@RequestHeader(value =
	 * "tenantId") String tenantId,
	 * 
	 * @RequestBody String updateParams) throws JSONException, JsonParseException,
	 * JsonMappingException, IOException {
	 * logger.debug("Entering updateUserGroupAssociation()");
	 * 
	 * userGroupAssociationDTO.setTenantId(tenantId); ObjectMapper mapper = new
	 * ObjectMapper();
	 * 
	 * @SuppressWarnings("unchecked") Map<String, Object> updateParams =
	 * mapper.convertValue(userGroupAssociationDTO, Map.class); //
	 * .writeValueAsString(secureDataRegistrationDTO);
	 * 
	 * GroupProfile groupProfile = wrapperService.updateGroupProfile(updateParams,
	 * userGroupAssociationDTO.getGroupId(), tenantId);
	 * System.out.println("updateGroupProfile" + groupProfile);
	 * logger.debug("Exit updateGroupProfile()"); return new
	 * ResponseEntity<GroupProfile>(groupProfile, HttpStatus.OK); }
	 */

}
