package com.newgen.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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

import com.newgen.dto.AnnotationDTO;
import com.newgen.exception.CustomException;
import com.newgen.model.Annotation;
import com.newgen.model.ValidationError;
import com.newgen.service.AnnotationService;
import com.newgen.validation.ValidationErrorBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/annotations")
@Api(value = "Annotation", description = "Operations for Annotations")
public class AnnotationController extends ExceptionThrower {
	
	private static final Logger logger = LoggerFactory.getLogger(AnnotationController.class);

	@Autowired
	AnnotationService annotationService;
	
	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create an annotation", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Annotation created", response = Annotation.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<Annotation> createAnnotation(@Valid @RequestBody AnnotationDTO annotationDTO
			,@RequestHeader(value="tenantId") String tenantId) throws CustomException {
		if (tenantId==null){
			throwInvalidTenantException();
		}
		logger.debug("Entering createAnnotation()");
		
		Annotation annotation = new Annotation(null, annotationDTO.getAnnotationName(), annotationDTO.getAnnotationType(), 
				annotationDTO.getComments(),annotationDTO.getAnnotationGroupName(), annotationDTO.getAnnotationBuffer().toString(),
				annotationDTO.getAnnotationData(), annotationDTO.getOwnerName(),annotationDTO.getAccessType(), 
				annotationDTO.getDocumentId(), annotationDTO.getPageNo(), annotationDTO.getOwnerId(), new Date(), null, null,tenantId);
		
		annotation= annotationService.insert(annotation);
		logger.debug("Exit createAnnotation()");
		return new ResponseEntity<Annotation>(annotation, HttpStatus.CREATED);
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
	@ApiOperation(value = "Get Annotation By docId+pageNo or annotationId")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Annotation list", response = Annotation.class, responseContainer = "List"),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<List<Annotation>> searchAnnotations(HttpServletRequest request) throws CustomException {
		String tenantId = null;
		if (request.getHeader("tenantId") != null) {
			tenantId = request.getHeader("tenantId").toString();
		}
		else{
			throwInvalidTenantException();
		}
		logger.debug("Entering searchAnnotations()");
		List<Annotation> annotationList = annotationService.search(request.getParameterMap(),tenantId);	
		logger.debug("Exit searchAnnotations()");
		return new ResponseEntity<List<Annotation>>(annotationList, HttpStatus.FOUND);
	}
	
	/*@RequestMapping(method = RequestMethod.GET, value = "/fetchById" , produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Annotation By annotationId")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Annotation ", response = Annotation.class),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> searchAnnotationById( @RequestParam(value="id", required=true) String id
			) throws CustomException {
		logger.debug("Entering searchAnnotationById()");
		ResponseEntity<String> response = wrapperService.searchAnnotations(id);
		logger.debug("Exit searchAnnotationById()");
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}*/

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a annotation", produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Annotation deleted"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists."),
			@ApiResponse(code = 503, message = "Database connection failure"),
			@ApiResponse(code = 400, message = "Exception Message") })
	public ResponseEntity<String> deleteAnnotation(@PathVariable("id") String id,@RequestHeader(value="tenantId") String tenantId,
			@RequestParam(value = "version", required = false) Long version) throws CustomException {
		if (tenantId==null) {
			throwInvalidTenantException();
		}
		logger.debug("Entering deleteAnnotation()");
		ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		annotationService.delete(id,tenantId);
		logger.debug("Exit deleteAnnotation()");
		return new ResponseEntity<>(response.getBody(), HttpStatus.NO_CONTENT);

	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update Annotation Info")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Annotation updated", response = Annotation.class),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 423, message = "Lock Already exists"),
			@ApiResponse(code = 503, message = "Database connection failure") })
	public ResponseEntity<Annotation> updateAnnotation(HttpServletRequest request,@RequestBody Map<String, String> updateFolderParams,@RequestHeader(value="tenantId") String tenantId, @PathVariable("id") String id,
			@RequestParam(value = "version", required = false) Long version) throws CustomException {
		if (request.getHeader("tenantId") != null) {
			tenantId = request.getHeader("tenantId").toString();
		}
		else
		{
			throwInvalidTenantException();
		}
		logger.debug("Entering updateAnnotation()");
		ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
		
		Annotation updatedAnnotation = annotationService.update(id ,updateFolderParams,tenantId);
		
		//response = wrapperService.updateAnnotation(id, version, updateFolderParams,tenantId);
		/*if (checkIfAnnotation(id)) {
			response = wrapperService.updateAnnotation(id, version, updateFolderParams);
		} else {
			throwInvalidAnnotationException();
		}*/
		logger.debug("Exit updateAnnotation()");
		return new ResponseEntity<Annotation>(updatedAnnotation, response.getStatusCode());
	}

	/*public boolean checkIfAnnotation(String id) throws CustomException {
		ResponseEntity<String> response = wrapperService.checkIfAnnotation(id);
		if (response.getStatusCode() == HttpStatus.OK) {
			String responseBody = response.getBody();
			JSONArray jsonArray = new JSONArray(responseBody);
			return jsonArray.length() != 0;
		} else {
			throwUnknownErrorException();
		}
		return false;
	}*/

}
