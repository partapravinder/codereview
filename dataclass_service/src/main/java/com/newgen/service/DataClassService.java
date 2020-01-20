package com.newgen.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.DataClassDao;
import com.newgen.dto.AggResultObjDTO;
import com.newgen.exception.CustomException;
import com.newgen.model.DataClass;
import com.newgen.model.InOutParameters;
import com.newgen.model.LogEntity;
import com.newgen.model.ReportsDataResponse;

@Service
public class DataClassService extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(DataClassService.class);

//	@Autowired
//  private EurekaUrlResolver eurekaUrlResolver;

//    @Value("${service.DataClass.serviceId}")
//    private String DataClassServiceId;

	@Autowired
	DataClassDao dataClassDao;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${logging.service.url}")
	private String loggingServiceUrl;

	public DataClass insert(DataClass dataClass) throws CustomException {
		logger.debug("Creating DataClass");
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = dataClassDao.insert(dataClass);
		long endTime = System.nanoTime();
		callLoggingService(dataClass.getTenantId(), null, "CosmosDB", startTime, endTime,
				inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "POST",
				"DataClassService");
		return inOutParameters.getDataclass();
	}

	public List<DataClass> bulkInsert(List<DataClass> dataClasses, String tenantId) throws CustomException {
		logger.debug("Creating DataClasses");
		Date date = new Date();
		dataClasses.stream().forEach(dataClass -> {
			dataClass.setTenantId(tenantId);
			dataClass.setAccessDateTime(date);
			dataClass.setRevisedDateTime(date);
			dataClass.setCreationDateTime(date);
			dataClass.setFieldCount(dataClass.getDataFields().size());
		});
		System.out.println(dataClasses);
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = dataClassDao.insertAll(dataClasses);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "POST", "DataClassService");
		return inOutParameters.getDataClassList();
	}

	public DataClass findById(String id, String tenantId) throws CustomException {
		logger.debug("Finding a DataClass by id");
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = dataClassDao.findById(id, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "DataClassService");
		return inOutParameters.getDataclass();
	}

	public List<DataClass> search(Map<String, String[]> allRequestParams, String tenantId) throws CustomException {
		logger.debug("Searching for DataClasses based on : " + allRequestParams);
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = dataClassDao.findAllDataClasses(allRequestParams, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "DataClassService");
		return inOutParameters.getDataClassList();
	}

	public DataClass update(String id, String updateDataClassParams, String tenantId)
			throws CustomException, JsonParseException, JsonMappingException, IOException {
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = dataClassDao.findMergeAndModify(id, updateDataClassParams, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "PUT", "DataClassService");
		DataClass dataClass = inOutParameters.getDataclass();
		if (dataClass == null) {
			long sT = System.nanoTime();
			InOutParameters inOutParams = dataClassDao.findById(id, tenantId);
			dataClass = inOutParams.getDataclass();
			long eT = System.nanoTime();
			callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
					inOutParams.getResponsePayloadSize(), "GET", "DataClassService");
			if (dataClass == null) {
				throwDataClassNotFoundException();
			}
		}
		return dataClass;
	}

	public DataClass findByName(String name, String tenantId) {
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = dataClassDao.findByDataClassName(name, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "DataClassService");
		return inOutParameters.getDataclass();
	}

	public List<DataClass> findDataClassesByName(String name, String tenantId) {
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = dataClassDao.findDataClassesByName(name, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "DataClassService");
		return inOutParameters.getDataClassList();
	}

	public DataClass deleteDataclass(String id, String tenantId) throws CustomException {

		// TODO Find dataClass attached to Folder/Content/Cabinet, if associated throw
		// exception, dataClass meta-data exist. It cannot be deleted.

		// Search in Folders/Content/Cabinet by dataclass id, if count is more then zero
		// do it.
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = dataClassDao.findAndRemoveById(id, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "DELETE", "DataClassService");
		DataClass dataClass = inOutParameters.getDataclass();
		if (dataClass == null) {
			throwDataClassNotFoundException();
		}

		return dataClass;
	}

	public DataClass deleteDataClassField(String id, String fieldName, String tenantId)
			throws CustomException, JsonParseException, JsonMappingException, IOException {

		// TODO Find dataClass attached to Folder/Content/Cabinet, if associated throw
		// exception, dataClass meta-data exist. It cannot be deleted.

		// Search in Folders/Content/Cabinet by dataclass id, if count is more then zero
		// do it.
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = dataClassDao.findAndRemoveByIdAndFieldName(id, fieldName, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "DELETE", "DataClassService");
		DataClass dataClass = inOutParameters.getDataclass();
		if (dataClass == null) {
			throwDataClassNotFoundException();
		}
		return dataClass;
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
	
	public ReportsDataResponse reportsDataByField(String dataClassField,String month,String year,String tenantId)
	{
		List<AggResultObjDTO>  aggResultObjDTO=dataClassDao.findByKey(dataClassField,month,year,tenantId);
		ReportsDataResponse reportsDataResponse=new ReportsDataResponse();
		Map<String,Integer> countOfField=new HashMap<String,Integer>();
		for(AggResultObjDTO result:aggResultObjDTO)
		{
			countOfField.put(result.get_id(), result.getCount());
		}
		reportsDataResponse.setFileReportsData(countOfField);
		return reportsDataResponse;
	}

}
