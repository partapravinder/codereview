package com.newgen.dao.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.constants.Constants;
import com.newgen.dao.DataClassDao;
import com.newgen.dto.AggResultObjDTO;
import com.newgen.model.DataClass;
import com.newgen.model.DataField;
import com.newgen.model.InOutParameters;
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
	public InOutParameters insert(DataClass dataClass) {
		logger.debug("--------- " + dataClass);
		DataClass f = null;
		InOutParameters inOutParameters = new InOutParameters();
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
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(dataClass).totalSize()) / 1024.0);
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(f).totalSize()) / 1024.0);
		inOutParameters.setDataclass(f);
		return inOutParameters;
	}

	@Override
	public InOutParameters insertAll(List<DataClass> dataClasses) {
		logger.debug("--------- " + dataClasses);
		List<DataClass> f = null;
		InOutParameters inOutParameters = new InOutParameters();
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
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(dataClasses).totalSize()) / 1024.0);
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(f).totalSize()) / 1024.0);
		inOutParameters.setDataClassList(f);
		return inOutParameters;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InOutParameters findById(String id, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
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
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(dataClass).totalSize()) / 1024.0);
		inOutParameters.setDataclass(dataClass);
		return inOutParameters;
	}

	@SuppressWarnings("unchecked")
	public InOutParameters findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
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
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(dataClass).totalSize()) / 1024.0);
		inOutParameters.setDataclass(dataClass);
		return inOutParameters;
	}

	// @SuppressWarnings("unchecked")
	@Override
	public InOutParameters findAndRemoveByIdAndFieldName(String id, String fieldName, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {

		DataClass existingDataClass = findById(id, tenantId).getDataclass();
		InOutParameters inOutParameters = new InOutParameters();
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
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(updatableDataClass).totalSize()) / 1024.0);
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(dataClass).totalSize()) / 1024.0);
		inOutParameters.setDataclass(dataClass);
		return inOutParameters;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InOutParameters findAllDataClasses(Map<String, String[]> paramMap, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
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
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(dataClasses).totalSize()) / 1024.0);
		inOutParameters.setDataClassList(dataClasses);
		return inOutParameters;
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
	public InOutParameters findMergeAndModify(String id, String updateDataClassParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(id));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		JSONObject updatedParamsJson = new JSONObject(updateDataClassParams);

		ObjectMapper obj_mapper = new ObjectMapper();
		try {
			if (updatedParamsJson.has("dataFields")) {
				DataClass existingDataClass = findById(id, tenantId).getDataclass();
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
				if (updatableDataFields != null && updatableDataFields.length() != 0) {
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
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(updatedDataClass).totalSize()) / 1024.0);
		inOutParameters.setDataclass(updatedDataClass);
		return inOutParameters;
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
	public InOutParameters findByDataClassName(String dataClassName, String tenantId) {
		DataClass dataClass = null;
		InOutParameters inOutParameters = new InOutParameters();
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
		inOutParameters.setRequestPayloadSize(((GraphLayout.parseInstance(dataClassName).totalSize()) / 1024.0)
				+ ((GraphLayout.parseInstance(tenantId).totalSize()) / 1024.0));
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(dataClass).totalSize()) / 1024.0);
		inOutParameters.setDataclass(dataClass);
		return inOutParameters;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InOutParameters findDataClassesByName(String dataClassName, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
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
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(dataClassName).totalSize()) / 1024.0);
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(dataClasses).totalSize()) / 1024.0);
		inOutParameters.setDataClassList(dataClasses);
		return inOutParameters;
	}

	public List<AggResultObjDTO> findByKey(String dataClassField, String month, String year, String tenantId) {

		List<AggregationOperation> aggregationOperation = new ArrayList<AggregationOperation>();
		String param = "dataclass." + dataClassField;

		aggregationOperation.add(Aggregation.match(Criteria.where(param).ne(null).and("tenantId").is(tenantId)));
		if (month != null && year != null) {
			aggregationOperation.add(
					Aggregation.project().andInclude("dataclass").and(DateOperators.dateOf("$creationDateTime").month())
							.as("month").and(DateOperators.dateOf("$creationDateTime").year()).as("year"));
		} else if (month == null && year != null) {
			aggregationOperation.add(Aggregation.project().andInclude("dataclass")
					.and(DateOperators.dateOf("$creationDateTime").year()).as("year"));
		}
		if (month != null && year != null) {
			aggregationOperation.add(Aggregation
					.match(Criteria.where("month").is(Integer.parseInt(month)).and("year").is(Integer.parseInt(year))));
		} else if (month == null && year != null) {
			aggregationOperation.add(Aggregation.match(Criteria.where("year").is(Integer.parseInt(year))));
		}

		aggregationOperation
				.add(Aggregation.project().and(StringOperators.valueOf(param).split(",")).as("DataClassField"));
		aggregationOperation.add(Aggregation.unwind("$DataClassField"));
		aggregationOperation.add(Aggregation.group("DataClassField").count().as("count"));
		Aggregation agg = Aggregation.newAggregation(aggregationOperation);
		System.out.println("Query  ==>>[" + agg.toString() + "]");
		AggregationResults<AggResultObjDTO> data = mongoTemplate.aggregate(agg, "content", AggResultObjDTO.class);

		List<AggResultObjDTO> result = data.getMappedResults();
		return result;
	}

}
