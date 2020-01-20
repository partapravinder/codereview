package com.newgen.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.model.DataClass;

public interface DataClassDao {
	public DataClass findAndRemoveById(String id, String tenantId);

	public DataClass findAndRemoveByIdAndFieldName(String id, String fieldName, String tenantId)
			throws JsonParseException, JsonMappingException, IOException;

	public DataClass findAndModify(String id, String updateDataClassParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException;

	public DataClass findMergeAndModify(String id, String updateDataClassParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException;

	public List<DataClass> findAllDataClasses(Map<String, String[]> paramMap, String tenantId);

	public DataClass findByDataClassName(String DataClassName, String tenantId);

	public List<DataClass> findDataClassesByName(String DataClassName, String tenantId);

	public DataClass insert(DataClass dataClass);

	public DataClass findById(String id, String tenantId);

	List<DataClass> insertAll(List<DataClass> dataClasses);

}
