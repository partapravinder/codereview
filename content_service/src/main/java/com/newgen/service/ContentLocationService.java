package com.newgen.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.ContentLocationDao;
import com.newgen.dto.FolderDTO; 
import com.newgen.exception.CustomException;
import com.newgen.model.ContentLocation;

@Service
public class ContentLocationService extends ExceptionThrower {

	@Autowired
	ContentLocationDao contentLocationDao;

	@Value("${storage.service.url}")
	private String storageServiceUrl;
	
//	@Value("${service.storage.serviceId}")
//	private String storageServiceId;

//	@Autowired
//	private EurekaUrlResolver eurekaUrlResolver;
	

	private static final Logger logger = LoggerFactory.getLogger(ContentLocationService.class);

	public ContentLocation insert(ContentLocation contentLocation,String tenantId) throws CustomException {
		logger.debug("Creating " + contentLocation);
		return contentLocationDao.insert(contentLocation,tenantId);
	}

	public ContentLocation findById(String id,String tenantId) throws CustomException {
		logger.debug("Finding content location by id: " + id);
		return contentLocationDao.findById(id,tenantId);
	}
	
	public List<ContentLocation> findByLocationIds(List<String> ids,String tenantId) throws CustomException {
		logger.debug("Finding content location by id: " + ids);
		return contentLocationDao.findByStorageLocationIds(ids, tenantId);
	}
	
	public ContentLocation findAndDeleteByLocationId(String locationId,String tenantId) throws CustomException {
		logger.debug("Finding and deleting content location by locationId: " + locationId);
		return contentLocationDao.findAndDeleteByLocationId(locationId,tenantId);
	}


	public ContentLocation decreaseCount(String id,String tenantId) throws CustomException {

		ContentLocation contentLocation = findById(id,tenantId);
		// Check if contentlocation is null or not
		if (contentLocation != null) {
			int sharedCount = contentLocation.getSharedCount();
			if (sharedCount == 1) {
				logger.debug("Marking for delete contentLocation with id: " + id);
				markDelete(id,tenantId);
			} else {
				logger.debug("Decreasing shared count for contentLocation with id: " + id);
				decrementSharedCount(id,tenantId);
			}
		} else {
			throwContentLocationNotFoundException();
		}
		return contentLocation;
	}

	public ContentLocation increaseCount(String id,String tenantId) throws CustomException {
		ContentLocation contentLocation = findById(id,tenantId);
		// Check if content is null or not
		if (contentLocation != null) {
			logger.debug("Increasing shared count for contentLocation with id: " + id);
			incrementSharedCount(id,tenantId);
		} else {
			logger.debug("ContentLocation not found with id: " + id);
			throwContentLocationNotFoundException();
		}
		return contentLocation;
	}

	public ContentLocation incrementSharedCount(String id,String tenantId) {
		return contentLocationDao.findAndIncrementSharedCount(id,tenantId);
	}

	public ContentLocation decrementSharedCount(String id,String tenantId) {
		return contentLocationDao.findAndDecrementSharedCount(id,tenantId);
	}

	public void delete(String id, String version, String locationId,String tenantId) throws CustomException, JSONException {
		logger.debug("Deleting ContentLocation with id: " + id + " and version: " + version);

		if (locationId != null && !locationId.isEmpty()) {
			// Delete storage location
			deleteStorageLocation(locationId,tenantId);

			// Delete the content location
			if (version == null || version.isEmpty()) {
				if (contentLocationDao.findAndRemoveById(id,tenantId) == null) {
					throwContentLocationNotFoundException();
				}
			} else {
				if (contentLocationDao.findAndRemoveByIdAndVersion(id, version,tenantId) == null) {
					ContentLocation contentLocation = contentLocationDao.findById(id,tenantId);
					if (contentLocation == null) {
						throwContentLocationNotFoundException();
					} else {
						if (!Long.toString(contentLocation.getVersion()).equalsIgnoreCase(version)) {
							throwVersionConflictException();
						} else {
							throwUnknownErrorException();
						}
					}
				}
			}
		} else {
			throwLocationIdNotPresent();
		}
	}

	public void deleteStorageLocation(String locationId,String tenantId) throws CustomException, JSONException {
		logger.debug("Calling storage service to delete locationId: " + locationId);
		//String storageServiceUrl = eurekaUrlResolver.procureUrl(storageServiceId);
		String url = storageServiceUrl + "/store/delete/" + locationId;
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("tenantId", tenantId);
		HttpEntity<FolderDTO> req = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, req, String.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			JSONObject json = new JSONObject(response.getBody());
			logger.debug("Response after storage service delete: " + response.getBody());
			String token = json.getString("token");
			if (token != null && !token.isEmpty()) {
				// Check delete status till its COMPLETED
				checkDeleteStatusForLocation(token,tenantId);
			}
		} else {
			logger.debug("Delete of location Id failed: " + locationId);
			throwFailedToDeleteStorageLocation();
		}

	}

	public void checkDeleteStatusForLocation(String token,String tenantId) throws CustomException, JSONException {
		RestTemplate restTemplate = new RestTemplate();
		String status = "";
		ResponseEntity<String> response;
		UriComponentsBuilder builder;
		do {
			// Query parameters
			//String storageServiceUrl = eurekaUrlResolver.procureUrl(storageServiceId);
			builder = UriComponentsBuilder.fromUriString(storageServiceUrl + "/store/status");
			// Add query parameter
			if (token != null && !token.isEmpty()) {
				builder.queryParam("token", token);
			}
			HttpHeaders headers = new HttpHeaders();
			headers.set("tenantId", tenantId);
			HttpEntity<FolderDTO> req = new HttpEntity<>(headers);
			response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, req,
					String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				JSONObject json = new JSONObject(response.getBody());
				status = json.getString("status");
				if ("COMPLETED".equalsIgnoreCase(status)) {
					// acknowledge the status
					builder = UriComponentsBuilder.fromUriString(storageServiceUrl + "/store/acknowledge");
					// Add query parameter
					if (token != null) {
						builder.queryParam("token", token);
					}
					restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, req,
							String.class);
				} else if ("FAILED".equalsIgnoreCase(status)) {
					throwFailedToDeleteStorageLocation();
				}
			} else {
				throwFailedToDeleteStorageLocation();
			}
		} while (!"COMPLETED".equalsIgnoreCase(status) && !"FAILED".equalsIgnoreCase(status));

	}

	public void markDelete(String id,String tenantId) throws CustomException {
		Map<String, String> deleteFlagParam = new HashMap<>();
		deleteFlagParam.put("deleted", "true");
		update(id, deleteFlagParam, null,tenantId);
	}

	public ContentLocation update(String id, Map<String, String> updateParams, Long version,String tenantId) throws CustomException {
		logger.debug("Updating ContentLocation with id: " + id + " and version: " + version);
		ContentLocation contentLocation = contentLocationDao.findAndModify(id, updateParams, version,tenantId);

		if (contentLocation == null) {
			contentLocation = contentLocationDao.findById(id,tenantId);
			if (contentLocation == null) {
				throwContentLocationNotFoundException();
			} else {
				if (version != null
						&& !Long.toString(contentLocation.getVersion()).equalsIgnoreCase(Long.toString(version))) {
					throwVersionConflictException();
				} else {
					throwUnknownErrorException();
				}
			}
		}
		return contentLocation;
	}

	public List<ContentLocation> findAllDeletedContentLocations() {
		logger.debug("Find all content locations marked for delete");
		return contentLocationDao.findAllDeletedContentLocations();
	}

	public List<ContentLocation> findAllDanglingContentLocations() {
		logger.debug("Find all dangling content locations");
		return contentLocationDao.findAllDanglingContentLocations();
	}

}
