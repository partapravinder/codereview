package com.newgen.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.dto.AggResultObjDTO;
import com.newgen.model.DataClass;
import com.newgen.model.InOutParameters;

public interface DataClassDao {
	public InOutParameters findAndRemoveById(String id, String tenantId);

	public InOutParameters findAndRemoveByIdAndFieldName(String id, String fieldName, String tenantId)
			throws JsonParseException, JsonMappingException, IOException;

	public DataClass findAndModify(String id, String updateDataClassParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException;

	public InOutParameters findMergeAndModify(String id, String updateDataClassParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException;

	public InOutParameters findAllDataClasses(Map<String, String[]> paramMap, String tenantId);

	public InOutParameters findByDataClassName(String DataClassName, String tenantId);

	public InOutParameters findDataClassesByName(String DataClassName, String tenantId);

	public InOutParameters insert(DataClass dataClass);

	public InOutParameters findById(String id, String tenantId);

	InOutParameters insertAll(List<DataClass> dataClasses);
	
	public List<AggResultObjDTO>  findByKey(String dataClassField,String month,String year,String tenantId); 

}
