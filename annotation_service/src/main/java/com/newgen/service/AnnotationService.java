package com.newgen.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.AnnotationDao;
import com.newgen.model.Annotation;
import com.newgen.model.InOutParameters;
import com.newgen.model.LogEntity;

@Service
public class AnnotationService extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationService.class);

	@Autowired
	AnnotationDao annotationDao;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${logging.service.url}")
	private String loggingServiceUrl;

	public Annotation insert(Annotation annotation) {
		logger.debug("Insering a new Annotation");
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = annotationDao.insert(annotation);
		long endTime = System.nanoTime();
		callLoggingService(annotation.getTenantId(), null, "CosmosDB", startTime, endTime,
				inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "POST",
				"AnnotationService");
		return inOutParameters.getAnnotation();
	}

	/*
	 * public Annotation find(String id) throws CustomException { return
	 * annotationDao.findAndModify(id); }
	 */

	public Annotation update(String id, Map<String, String> updateFolderParams, String tenantId) {
		logger.debug("Updating Annotation with id: " + id);
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = annotationDao.findAndModify(id, updateFolderParams, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "PUT", "AnnotationService");
		return inOutParameters.getAnnotation();
	}

	public void delete(String id, String tenantId) {
		logger.debug("Deleting Annotation with id: " + id);
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = annotationDao.findAndRemoveById(id, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "DELETE", "AnnotationService");
	}

	/*
	 * public List<Annotation> search(Map<String, String[]> allRequestParams) {
	 * logger.debug("Searching for Annotation with : " + allRequestParams); return
	 * annotationDao.findAllAnnotations(allRequestParams); }
	 */

	public List<Annotation> search(Map<String, String[]> allRequestParams, String tenantId) {
		logger.debug("Searching for Annotations  " + allRequestParams);
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = annotationDao.findAllAnnotations(allRequestParams, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "AnnotationService");
		return inOutParameters.getAnnotations();
	}

	public void callLoggingService(String tenantId, String userId, String logType, Long startTime, Long endTime,
			Double reqSize, Double resSize, String requestType, String serviceType) {
		HttpHeaders headers = new HttpHeaders();

		headers.set("tenantId", tenantId);
		headers.set("userId", userId);
		headers.set("Content-Type", "application/json");

		String apiurl = loggingServiceUrl + "/logging/saveLog";
		if (reqSize != null) {
			reqSize = Math.ceil(reqSize);
		}
		if (resSize != null) {
			resSize = Math.ceil(resSize);
		}
		LogEntity logEntity = new LogEntity(logType, requestType, serviceType, reqSize, resSize, startTime, endTime);
		HttpEntity<LogEntity> request = new HttpEntity<LogEntity>(logEntity, headers);
		restTemplate.exchange(apiurl, HttpMethod.POST, request, String.class);
	}
}
