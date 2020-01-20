package com.newgen.dao.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.constants.Constants;
import com.newgen.dao.DataClassDao;
import com.newgen.model.DataClass;
import com.newgen.model.DataField;
import com.newgen.repository.DataClassRepository;
import com.newgen.wrapper.service.WrapperMongoService;

import groovy.json.JsonException;

@Repository
public class DataClassDaoImpl implements DataClassDao, Constants {
	private static final Logger logger = LoggerFactory.getLogger(DataClassDaoImpl.class);
	@SuppressWarnings("rawtypes")
	@Autowired
	WrapperMongoService mongoTemplate;

	@Autowired
	DataClassRepository dataClassRepository;

	@Value("${pagination.batchSize}")
	int pagesize;

	public static final String DELETED_PARAM = "deleted";

	@Override
	public DataClass insert(DataClass dataClass) {
		logger.debug("--------- " + dataClass);
		DataClass f = null;
		try {
			f = dataClassRepository.insert(dataClass);
		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (f == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				f = dataClassRepository.insert(dataClass);
			}
			logger.debug(" " + f);
		}
		return f;
	}

	@Override
	public List<DataClass> insertAll(List<DataClass> dataClasses) {
		logger.debug("--------- " + dataClasses);
		List<DataClass> f = null;
		try {
			f = dataClassRepository.insert(dataClasses);
		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (f == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				f = dataClassRepository.insert(dataClasses);
			}
			logger.debug(" " + f);
		}
		return f;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataClass findById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		DataClass dataClass = null;
		try {
			dataClass = (DataClass) mongoTemplate.findOne(query, DataClass.class);
		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (dataClass == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				dataClass = (DataClass) mongoTemplate.findOne(query, DataClass.class);
			}
			logger.debug(" " + dataClass);
		}
		logger.debug("Found DataClass : " + dataClass);
		return dataClass;
	}

	@SuppressWarnings("unchecked")
	public DataClass findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		logger.debug("Deleting DataClass : " + id);
		DataClass dataClass = null;
		try {
			dataClass = (DataClass) mongoTemplate.findAndRemove(query, DataClass.class);
		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (dataClass == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				dataClass = (DataClass) mongoTemplate.findAndRemove(query, DataClass.class);
			}
			logger.debug(" " + dataClass);
		}
		logger.debug("Deleted DataClass : " + dataClass);
		return dataClass;
	}

	// @SuppressWarnings("unchecked")
	@Override
	public DataClass findAndRemoveByIdAndFieldName(String id, String fieldName, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {

		DataClass existingDataClass = findById(id, tenantId);
		List<DataField> dataFields = existingDataClass.getDataFields();

		Map<String, DataField> dataFieldMap = new HashMap<>();
		if (dataFields != null && dataFields.size() > 0) {
			dataFields.stream().forEach(dataField -> {
				dataFieldMap.put(dataField.getDataFieldIndexName(), dataField);
			});
		}

		List<DataField> updatableDataFields = dataFields.stream()
				.filter(dataField -> !fieldName.equals(dataField.getDataFieldIndexName())).collect(Collectors.toList());
		DataClass updatableDataClass = existingDataClass;
		updatableDataClass.setDataFields(updatableDataFields);
		updatableDataClass.setFieldCount(updatableDataClass.getDataFields().size());

		logger.debug("Deleting DataClass : " + id);
		DataClass dataClass = null;
		try {
			dataClass = (DataClass) findAndModify(id, (new JSONObject(updatableDataClass)).toString(), tenantId);
		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (dataClass == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				dataClass = (DataClass) findAndModify(id, (new JSONObject(updatableDataClass)).toString(), tenantId);
			}
			logger.debug(" " + dataClass);
		}
		logger.debug("Modified DataClass : " + dataClass);
		return dataClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DataClass> findAllDataClasses(Map<String, String[]> paramMap, String tenantId) {
		Query query = new Query();
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			try {
				logger.debug(entry.getKey() + "=>" + URLDecoder.decode(entry.getValue()[0], "UTF-8"));
				if ("dataClassName".equalsIgnoreCase(entry.getKey()) && entry.getValue()[0].toString().contains("*")) {
					query.addCriteria(Criteria.where(entry.getKey()).regex(toLikeRegex(entry.getValue()[0]), "i"));
				} else {
					query.addCriteria(
							Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0], "UTF-8")));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		List<DataClass> dataClasses = null;
		try {
			dataClasses = mongoTemplate.find(query, DataClass.class);
		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (dataClasses == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				dataClasses = mongoTemplate.find(query, DataClass.class);
			}
			logger.debug(" " + dataClasses);
		}
		return dataClasses;
	}

	private String toLikeRegex(String source) {
		logger.debug("S---->" + source);
		if (source.charAt(0) == '*' && source.charAt(source.length() - 1) != '*') {
			// logger.debug("First----}"+ source.charAt(0));
			// logger.debug("Last----}"+ source.charAt(source.length()-1));
			// logger.debug(source.replaceAll("\\*", "")+"$");
			return source.replaceAll("\\*", "") + "$";
		} else if (source.charAt(0) != '*' && source.charAt(source.length() - 1) == '*') {
			// logger.debug("First----}"+ source.charAt(0));
			// logger.debug("Last----}"+ source.charAt(source.length()-1));
			// logger.debug("^"+source.replaceAll("\\*", ""));
			return "^" + source.replaceAll("\\*", "");
		} else {
			// logger.debug("First----}"+ source.charAt(0));
			// logger.debug("Last----}"+ source.charAt(source.length()-1));
			// logger.debug(source.replaceAll("\\*", ""));
			return source.replaceAll("\\*", "");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataClass findMergeAndModify(String id, String updateDataClassParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(id));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		JSONObject updatedParamsJson = new JSONObject(updateDataClassParams);

		ObjectMapper obj_mapper = new ObjectMapper();
		try {
			if (updatedParamsJson.has("dataFields")) {
				DataClass existingDataClass = findById(id, tenantId);
				JSONObject existingDCJson = new JSONObject(existingDataClass);
				System.out.println(existingDCJson);
				Map<String, JSONObject> mapDf = new HashMap<String, JSONObject>();

				if (existingDCJson != null && existingDCJson.has("dataFields")) {
					for (int i = 0; i < existingDCJson.getJSONArray("dataFields").length(); i++) {
						mapDf.put(
								existingDCJson.getJSONArray("dataFields").getJSONObject(i)
										.getString("dataFieldIndexName"),
								existingDCJson.getJSONArray("dataFields").getJSONObject(i));
					}
				}

				for (int i = 0; i < updatedParamsJson.getJSONArray("dataFields").length(); i++) {
					mapDf.put(
							updatedParamsJson.getJSONArray("dataFields").getJSONObject(i)
									.getString("dataFieldIndexName"),
							updatedParamsJson.getJSONArray("dataFields").getJSONObject(i));
				}
				JSONArray updatableDataFields = new JSONArray();
				for (Map.Entry<String, JSONObject> entry : mapDf.entrySet()) {
					updatableDataFields.put(entry.getValue());
				}
				updatedParamsJson.remove("dataFields");
				updatedParamsJson.put("dataFields", updatableDataFields);
				if (updatableDataFields != null
						&& updatableDataFields.length()!=0) {
					updatedParamsJson.put("fieldCount", updatableDataFields.length());
				} 
				/*
				 * else { updatedParamsJson.put("fieldCount",
				 * updatableDataFields.getJSONArray("dataFields").length()); }
				 */
			}
		} catch (JsonException e) {
			e.printStackTrace();
		}
		Map<String, String> dataClassMap = null;
		try {
			dataClassMap = obj_mapper.readValue(updatedParamsJson.toString(), Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Map.Entry<String, String> entry : dataClassMap.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) || VERSION_PARAM.equalsIgnoreCase(entry.getKey())
					|| "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())
					|| "parentDataClass".equalsIgnoreCase(entry.getKey())
					|| "ownerName".equalsIgnoreCase(entry.getKey()) || "ownerId".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			update.set(entry.getKey(), entry.getValue());
		}

		DataClass updatedDataClass = null;
		try {
			updatedDataClass = (DataClass) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), DataClass.class);
		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (updatedDataClass == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				updatedDataClass = (DataClass) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), DataClass.class);
			}
			logger.debug(" " + updatedDataClass);
		}
		logger.debug("Updated DataClass - " + updatedDataClass);
		return updatedDataClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataClass findAndModify(String id, String updateDataClassParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(id));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		JSONObject updatedParamsJson = new JSONObject(updateDataClassParams);

		ObjectMapper obj_mapper = new ObjectMapper();

		Map<String, String> dataClassMap = null;
		try {
			dataClassMap = obj_mapper.readValue(updatedParamsJson.toString(), Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Map.Entry<String, String> entry : dataClassMap.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) || VERSION_PARAM.equalsIgnoreCase(entry.getKey())
					|| "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())
					|| "parentDataClass".equalsIgnoreCase(entry.getKey())
					|| "ownerName".equalsIgnoreCase(entry.getKey()) || "ownerId".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			update.set(entry.getKey(), entry.getValue());
		}

		DataClass updatedDataClass = null;
		try {
			updatedDataClass = (DataClass) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), DataClass.class);
		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (updatedDataClass == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				updatedDataClass = (DataClass) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), DataClass.class);
			}
			logger.debug(" " + updatedDataClass);
		}
		logger.debug("Updated DataClass - " + updatedDataClass);
		return updatedDataClass;
	}

	@Override
	public DataClass findByDataClassName(String dataClassName, String tenantId) {
		DataClass dataClass = null;
		try {
			dataClass = dataClassRepository.findByDataClassName(dataClassName, tenantId);
		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (dataClass == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				dataClass = dataClassRepository.findByDataClassName(dataClassName, tenantId);
			}
			logger.debug(" " + dataClass);
		}
		return dataClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DataClass> findDataClassesByName(String dataClassName, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("dataClassName").regex(dataClassName));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		List<DataClass> dataClasses = null;
		try {
			dataClasses = mongoTemplate.find(query, DataClass.class);
		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (dataClasses == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				dataClasses = mongoTemplate.find(query, DataClass.class);
			}
			logger.debug(" " + dataClasses);
		}
		return dataClasses;
	}

}
