package com.newgen.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.DataClassDao;
import com.newgen.exception.CustomException;
import com.newgen.model.DataClass;

@Service
public class DataClassService extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(DataClassService.class);

//	@Autowired
//  private EurekaUrlResolver eurekaUrlResolver;

//    @Value("${service.DataClass.serviceId}")
//    private String DataClassServiceId;

	@Autowired
	DataClassDao dataClassDao;

	public DataClass insert(DataClass dataClass) throws CustomException {
		logger.debug("Creating DataClass");
		return dataClassDao.insert(dataClass);
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
		return dataClassDao.insertAll(dataClasses);
	}

	public DataClass findById(String id, String tenantId) throws CustomException {
		logger.debug("Finding a DataClass by id");
		return dataClassDao.findById(id, tenantId);
	}

	public List<DataClass> search(Map<String, String[]> allRequestParams, String tenantId) throws CustomException {
		logger.debug("Searching for DataClasses based on : " + allRequestParams);
		return dataClassDao.findAllDataClasses(allRequestParams, tenantId);
	}

	public DataClass update(String id, String updateDataClassParams, String tenantId)
			throws CustomException, JsonParseException, JsonMappingException, IOException {
		DataClass dataClass = dataClassDao.findMergeAndModify(id, updateDataClassParams, tenantId);

		if (dataClass == null) {
			dataClass = dataClassDao.findById(id, tenantId);
			if (dataClass == null) {
				throwDataClassNotFoundException();
			}
		}
		return dataClass;
	}

	public DataClass findByName(String name, String tenantId) {
		return dataClassDao.findByDataClassName(name, tenantId);
	}

	public List<DataClass> findDataClassesByName(String name, String tenantId) {
		return dataClassDao.findDataClassesByName(name, tenantId);
	}

	public DataClass deleteDataclass(String id, String tenantId) throws CustomException {

		// TODO Find dataClass attached to Folder/Content/Cabinet, if associated throw
		// exception, dataClass meta-data exist. It cannot be deleted.

		// Search in Folders/Content/Cabinet by dataclass id, if count is more then zero
		// do it.
		DataClass dataClass = dataClassDao.findAndRemoveById(id, tenantId);
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
		DataClass dataClass = dataClassDao.findAndRemoveByIdAndFieldName(id, fieldName, tenantId);
		if (dataClass == null) {
			throwDataClassNotFoundException();
		}
		return dataClass;
	}

}
