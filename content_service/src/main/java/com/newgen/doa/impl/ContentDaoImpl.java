package com.newgen.doa.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.dao.ContentDao;
import com.newgen.model.Content;
import com.newgen.model.Folder;
import com.newgen.repository.ContentRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class ContentDaoImpl implements ContentDao {
	private static final Logger logger = LoggerFactory.getLogger(ContentDaoImpl.class);

	@SuppressWarnings("rawtypes")
	@Autowired
	WrapperMongoService mongoTemplate;

	@Autowired
	ContentRepository contentRepository;

	@Value("${pagination.batchSize}")
	int pagesize;

	// public static final String VERSION_PARAM = "version";

//	public static final String DELETED_PARAM = "deleted";

//	public static final String REVISEDDATETIME_PARAM = "revisedDateTime";
//	public static final String ACCESSDATETIME_PARAM = "accessDateTime";
//	public static final String CHECKEDOUTTIME_PARAM = "checkedOutTime";

	public Content insert(Content content, String tenantId) {
		logger.debug("insert content=>" + content.toString());
		try {
			return contentRepository.insert(content);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return contentRepository.insert(content);
		}
	}

	@SuppressWarnings("unchecked")
	public Content findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		// Content content = (Content) mongoTemplate.findAndRemove(query,
		// Content.class);

		Content content = null;
		try {
			content = (Content) mongoTemplate.findAndRemove(query, Content.class);

		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {

			if (content == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				content = (Content) mongoTemplate.findAndRemove(query, Content.class);
			}
			logger.debug("Deleted Content : " + content);
		}

		return content;
	}

	@SuppressWarnings("unchecked")
	public Content findAndRemoveByIdAndVersion(String id, BigDecimal version, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id).andOperator(Criteria.where(Content.VERSION_PARAM).is(version)));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		// Content content = (Content) mongoTemplate.findAndRemove(query,
		// Content.class);

		Content content = null;
		try {
			content = (Content) mongoTemplate.findAndRemove(query, Content.class);

		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (content == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				content = (Content) mongoTemplate.findAndRemove(query, Content.class);
			}
			logger.debug("Deleted Content : " + content);
		}
		logger.debug("Deleted Content : " + content);
		return content;
	}

	@SuppressWarnings("unchecked")
	public Content findOne(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("_id").is(id.trim()).andOperator(Criteria.where(Content.DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		return (Content) mongoTemplate.findOne(query, Content.class);
	}

	@SuppressWarnings("unchecked")
	public Content findUncommitted(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id).andOperator(Criteria.where(Content.DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		return (Content) mongoTemplate.findOne(query, Content.class);
	}

	@SuppressWarnings("unchecked")
	public List<Content> findAllContents(Map<String, String[]> paramMap, String tenantId) {
		Query query = new Query();
		boolean ignoreFlag = false;
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			if ("ignoreCommittedFlag".equalsIgnoreCase(entry.getKey())) {
				ignoreFlag = true;
				continue;
			} else if ("parentFolderId".equalsIgnoreCase(entry.getKey())) {
				query.addCriteria(Criteria.where("parentFolderId").is(entry.getValue()[0]));
				continue;
			} else if ("name".equalsIgnoreCase(entry.getKey()) && entry.getValue()[0].toString().contains("*")) {
				query.addCriteria(Criteria.where(entry.getKey()).regex(toLikeRegex(entry.getValue()[0]), "i"));
				continue;
			}

			query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()[0]));
		}
		query.addCriteria(Criteria.where(Content.DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		if (!ignoreFlag)
			query.addCriteria(Criteria.where("flag").is("COMMITTED"));

		return mongoTemplate.find(query, Content.class);
	}

	private String toLikeRegex(String source) {
		// logger.debug("S---->" + source);
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
	public List<Content> findAllContentsByPage(Map<String, String[]> paramMap, String tenantId, int pageNo) {
		Query query = new Query();
		boolean ignoreFlag = false;

		final Pageable pageableRequest = PageRequest.of(pageNo, pagesize);
		query.with(pageableRequest);
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			if ("ignoreCommittedFlag".equalsIgnoreCase(entry.getKey())) {
				ignoreFlag = true;
				continue;
			}
			query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()[0]));
		}

		query.addCriteria(Criteria.where(Content.DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		if (!ignoreFlag)
			query.addCriteria(Criteria.where("flag").is("COMMITTED"));

		return mongoTemplate.find(query, Content.class);
	}

	@Override
	public Content findAndModify(String id, String updateContentParams, BigDecimal version, String tenantId) {
		return findAndModify(id, updateContentParams, version, false, tenantId, false);
	}

	@Override
	public Content findAndModify(String id, String updateContentParams, String tenantId) {
		return findAndModify(id, updateContentParams, null, false, tenantId, false);
	}

	@Override
	public Content findAndCheckOut(String id, String updateContentParams, String tenantId) {
		return findAndModify(id, updateContentParams, null, false, tenantId, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Content findAndModify(String id, String updateContentParams, BigDecimal version, boolean ignoreCommittedFlag,
			String tenantId, boolean checkout) {

		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		if (version == null) {
			query.addCriteria(Criteria.where("_id").is(id).andOperator(Criteria.where("latest").is(true)));
			;
			// query.addCriteria(Criteria.where("latest").is(true));
		} else {
			query.addCriteria(
					Criteria.where("_id").is(id).andOperator(Criteria.where(Content.VERSION_PARAM).is(version)));
		}
		query.addCriteria(Criteria.where(Content.DELETED_PARAM).ne("true"));
		if (!ignoreCommittedFlag)
			query.addCriteria(Criteria.where("flag").is("COMMITTED"));

		Update update = new Update();
		update.set(Content.REVISEDDATETIME_PARAM, new Date());
		update.set(Content.ACCESSDATETIME_PARAM, new Date());

		if (checkout) {
			update.set(Content.CHECKEDOUTTIME_PARAM, new Date());
		}

		JSONObject updatedParamsJson = new JSONObject(updateContentParams);
		if (updateContentParams == null) {
			update.unset("metadata");

		} else if (updatedParamsJson.has("removeDataclass") && updatedParamsJson.get("removeDataclass") != null
				&& updatedParamsJson.getBoolean("removeDataclass")) {
			update.unset("dataclass");
			update.unset("dataClassText");
		} else {
			ObjectMapper obj_mapper = new ObjectMapper();

			// Handle metadata
			if (updatedParamsJson.has("metadata") && updatedParamsJson.get("metadata") != null) {
				Map<String, String> metamap = null;
				try {
					metamap = obj_mapper.readValue(updatedParamsJson.get("metadata").toString(), Map.class);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// Get existing metadata map
				Content content_temp = findOne(id, tenantId);
				if (content_temp.getMetadata() != null) {// If already metadata exists, append new map
					Map<String, String> Existing_metamap = content_temp.getMetadata();
					Existing_metamap.putAll(metamap);
					update.set("metadata", Existing_metamap);
				} else {// add new map metadata
					update.set("metadata", metamap);
				}
				updatedParamsJson.remove("metadata");
			}

			// Handle dataclass
			if (updatedParamsJson.has("dataclass") && updatedParamsJson.get("dataclass") != null) {
				Map<String, String> dataclassmap = null;
				try {
					dataclassmap = obj_mapper.readValue(updatedParamsJson.get("dataclass").toString(), Map.class);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// Get existing dataclass map
				Content content_temp = findOne(id, tenantId);
				if (content_temp.getDataclass() != null) {// If already dataclass exists, append new map
					Map<String, String> existingDataclassmap = content_temp.getDataclass();
					existingDataclassmap.putAll(dataclassmap);
					update.set("dataclass", existingDataclassmap);
					dataclassmap = existingDataclassmap;
				} else {// add new map dataclass
					update.set("dataclass", dataclassmap);
				}
				StringBuffer dataClassText = new StringBuffer();
				if(dataclassmap != null) {
					Iterator<Entry<String, String>> itr = dataclassmap.entrySet().iterator();
					while(itr.hasNext()) {
						Entry<String, String> entry = itr.next();
						String key = entry.getKey(); 
						String value = entry.getValue();
						if(key.compareToIgnoreCase("id") != 0 && value != null) {
							dataClassText.append(value.toLowerCase() + " ");
						}
					}
				}
				update.set("dataClassText", dataClassText.toString());
				updatedParamsJson.remove("dataclass");
			}

			Map<String, String> contentMap = null;
			try {
				contentMap = obj_mapper.readValue(updatedParamsJson.toString(), Map.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (Map.Entry<String, String> entry : contentMap.entrySet()) {
				if ("id".equalsIgnoreCase(entry.getKey()) || Content.VERSION_PARAM.equalsIgnoreCase(entry.getKey())
						|| "parentFolderId".equalsIgnoreCase(entry.getKey())
						|| "contentLocationId".equalsIgnoreCase(entry.getKey())
						|| "creationDateTime".equalsIgnoreCase(entry.getKey())
						|| Content.ACCESSDATETIME_PARAM.equalsIgnoreCase(entry.getKey())
						|| Content.REVISEDDATETIME_PARAM.equalsIgnoreCase(entry.getKey())
						|| (checkout && Content.CHECKEDOUTTIME_PARAM.equalsIgnoreCase(entry.getKey()))
						|| "parentFolder".equalsIgnoreCase(entry.getKey())
						|| "contentLocation".equalsIgnoreCase(entry.getKey())) {
					continue;
				}
				if (entry.getValue() == null) {
					update.unset(entry.getKey());
				} else {
					update.set(entry.getKey(), entry.getValue());
				}
			}
		}
		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		Content updatedContent = (Content) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), Content.class);
		logger.debug("Updated Content - " + updatedContent);
		return updatedContent;
	}

	@SuppressWarnings("unchecked")
	public List<Content> findByParentFolderId(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("parentFolderId").is(id));
		query.addCriteria(Criteria.where(Content.DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		return mongoTemplate.find(query, Content.class);
	}

	@SuppressWarnings("unchecked")
	public List<Content> findByName(String name, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("name").regex(name));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where(Content.DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		return mongoTemplate.find(query, Content.class);
	}

	@Override
	public Content updateParentFolderId(String id, String targetFolderId, BigDecimal version, String tenantId) {
		Query query = new Query();
		if (version == null) {
			query.addCriteria(Criteria.where("_id").is(id));
		} else {
			query.addCriteria(
					Criteria.where("_id").is(id).andOperator(Criteria.where(Content.VERSION_PARAM).is(version)));
		}
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		Update update = new Update();
		update.set(Content.REVISEDDATETIME_PARAM, new Date());
		update.set(Content.ACCESSDATETIME_PARAM, new Date());
		update.set("parentFolderId", targetFolderId);

		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		@SuppressWarnings("unchecked")
		Content movedContent = (Content) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), Content.class);
		logger.debug("Moved content - " + movedContent);
		return movedContent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Content> findAllDeletedContents() {
		Query query = new Query();
		query.addCriteria(Criteria.where(Content.DELETED_PARAM).is("true"));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		// query.addCriteria(Criteria.where("tenantId").is(tenantId));
		return mongoTemplate.find(query, Content.class);
	}

	@Override
	public Content findAndRemoveContentLocation(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Update update = new Update();
		update.set(Content.REVISEDDATETIME_PARAM, new Date());
		update.set(Content.ACCESSDATETIME_PARAM, new Date());
		update.set("contentLocation", null);
		@SuppressWarnings("unchecked")
		Content updatedContent = (Content) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), Content.class);
		logger.debug("ContentLocation link removed content - " + updatedContent);
		return updatedContent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Content findContentWithContentLocation(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("contentLocationId").is(id));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		return (Content) mongoTemplate.findOne(query, Content.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Content findContentWithPrimaryContentId(String id, BigDecimal version, String tenantId) {
		logger.debug("entering findContentWithPrimaryContentId()");
		Query query = new Query();
		query.addCriteria(Criteria.where("primaryContentId").is(id));
		query.addCriteria(Criteria.where("version").is(version));
		// query.
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		return (Content) mongoTemplate.findOne(query, Content.class);
	}

	@Override
	public Content findContentWithPrimaryContentId(String id, String tenantId) {
		return findLatestOne(id, tenantId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Content findLatestOne(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("primaryContentId").is(id.trim())
				.andOperator(Criteria.where(Content.DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("latest").is(true));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		Content content = (Content) mongoTemplate.findOne(query, Content.class);
		return content;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Content findByToken(String token, String tenantId) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("token").is(token).andOperator(Criteria.where(Content.DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		return (Content) mongoTemplate.findOne(query, Content.class);
	}

	@Override
	public Content findAndDeleteByToken(String token, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("token").is(token));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		@SuppressWarnings("unchecked")
		Content content = (Content) mongoTemplate.findAndRemove(query, Content.class);
		logger.debug("Deleted Content : " + content);
		return content;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Content> findAllContentsByMetadata(String paramStr, String tenantId) {
		Query query = new Query();
		JSONObject paramJson = new JSONObject(paramStr);
		Iterator<String> keys = paramJson.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			if (paramJson.get(key) instanceof JSONObject) {
				JSONObject constraintsJson = new JSONObject(paramJson.get(key).toString());
				Iterator<String> constraintkeyset = constraintsJson.keys();
				while (constraintkeyset.hasNext()) {
					String constraintkey = constraintkeyset.next();
					if (constraintkey.equalsIgnoreCase("ne")) {
						query.addCriteria(
								Criteria.where("metadata." + key).ne(constraintsJson.getString(constraintkey)));
					} else if (constraintkey.equalsIgnoreCase("lte")) {
						query.addCriteria(
								Criteria.where("metadata." + key).lte(constraintsJson.getString(constraintkey)));
					} else if (constraintkey.equalsIgnoreCase("lt")) {
						query.addCriteria(
								Criteria.where("metadata." + key).lt(constraintsJson.getString(constraintkey)));
					} else if (constraintkey.equalsIgnoreCase("gte")) {
						query.addCriteria(
								Criteria.where("metadata." + key).gte(constraintsJson.getString(constraintkey)));
					} else if (constraintkey.equalsIgnoreCase("gt")) {
						query.addCriteria(
								Criteria.where("metadata." + key).gt(constraintsJson.getString(constraintkey)));
					}
				}
			} else
				query.addCriteria(Criteria.where("metadata." + key).regex(toLikeRegex(paramJson.getString(key)), "i"));
		}
		/*
		 * for (Map.Entry<String, String[]> entry : paramMap.entrySet()) { if
		 * (entry.getValue()[0].toString().contains("*")) {
		 * query.addCriteria(Criteria.where("metadata."+entry.getKey()).regex(
		 * toLikeRegex(entry.getValue()[0]),"i")); continue; }
		 * query.addCriteria(Criteria.where("metadata."+entry.getKey()).is(entry.
		 * getValue()[0])); }
		 */
		query.addCriteria(Criteria.where(Content.DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));

		return mongoTemplate.find(query, Content.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Content> findAllContentsByDataclass(String paramStr, String tenantId) {
		Query query = new Query();
		JSONObject paramJson = new JSONObject(paramStr);
		Iterator<String> keys = paramJson.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			if (paramJson.get(key) instanceof JSONObject) {
				JSONObject constraintsJson = new JSONObject(paramJson.get(key).toString());
				Iterator<String> constraintkeyset = constraintsJson.keys();
				while (constraintkeyset.hasNext()) {
					String constraintkey = constraintkeyset.next();
					if (constraintkey.equalsIgnoreCase("ne")) {
						query.addCriteria(
								Criteria.where("dataclass." + key).ne(constraintsJson.getString(constraintkey)));
					} else if (constraintkey.equalsIgnoreCase("lte")) {
						query.addCriteria(
								Criteria.where("dataclass." + key).lte(constraintsJson.getString(constraintkey)));
					} else if (constraintkey.equalsIgnoreCase("lt")) {
						query.addCriteria(
								Criteria.where("dataclass." + key).lt(constraintsJson.getString(constraintkey)));
					} else if (constraintkey.equalsIgnoreCase("gte")) {
						query.addCriteria(
								Criteria.where("dataclass." + key).gte(constraintsJson.getString(constraintkey)));
					} else if (constraintkey.equalsIgnoreCase("gt")) {
						query.addCriteria(
								Criteria.where("dataclass." + key).gt(constraintsJson.getString(constraintkey)));
					}
				}
			} else
				query.addCriteria(Criteria.where("dataclass." + key).regex(toLikeRegex(paramJson.getString(key)), "i"));
		}
		query.addCriteria(Criteria.where(Content.DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));

		return mongoTemplate.find(query, Content.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Content findUncommitedOne(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id).andOperator(Criteria.where(Content.DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		return (Content) mongoTemplate.findOne(query, Content.class);
	}

	@SuppressWarnings("unchecked")
	public List<Content> findAllContentsBySearchString(Map<String, Set<Object>> paramMap, String tenantId, boolean ftsEnabled) throws ParseException {
		boolean nameFilterExists = false;
		Query query = new Query();
		List<Criteria> andCriteriaList = new ArrayList<Criteria>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		for (Map.Entry<String, Set<Object>> entry : paramMap.entrySet()) {
			if ("creationDateTime".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					Iterator<Object> itr = entry.getValue().iterator();
					Date from = sdf.parse(itr.next().toString());
					Date to = sdf.parse(itr.next().toString());
					Date tmp = from;
					if(from.after(to)) {
						from = to;
						to = tmp;
					}
					andCriteriaList.add(Criteria.where(entry.getKey()).gte(from));
					andCriteriaList.add(Criteria.where(entry.getKey()).lte(to));
					continue;
				}
				continue;
			} else if ("revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					Iterator<Object> itr = entry.getValue().iterator();
					Date from = sdf.parse(itr.next().toString());
					Date to = sdf.parse(itr.next().toString());
					Date tmp = from;
					if(from.after(to)) {
						from = to;
						to = tmp;
					}
					andCriteriaList.add(Criteria.where(entry.getKey()).gte(from));
					andCriteriaList.add(Criteria.where(entry.getKey()).lte(to));
					continue;
				}
				continue;
			} else if ("contentLocationId".equalsIgnoreCase(entry.getKey()) && ftsEnabled) {
				andCriteriaList.add(Criteria.where(entry.getKey()).in(entry.getValue()));
				nameFilterExists = true;
				continue;
			} else if ("name".equalsIgnoreCase(entry.getKey()) && !ftsEnabled) {
				if(entry.getValue().size() > 0) {
					String value = entry.getValue().iterator().next().toString();
					if(!StringUtils.isEmpty(value)) {
						andCriteriaList.add(Criteria.where("name").regex(value,"i"));
						nameFilterExists = true;
					}
				}
				continue;
			}
		}
		if(!nameFilterExists) {
			return null;
		}
		andCriteriaList.add(Criteria.where(Content.DELETED_PARAM).ne("true"));
		andCriteriaList.add(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(new Criteria().andOperator(andCriteriaList.toArray(new Criteria[andCriteriaList.size()])));
		
		List<Content> list = null;
		try {
			list = mongoTemplate.find(query, Content.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		if(!ftsEnabled) {
			List<Content> listByKeyword = findAllContentByKeyword(paramMap, tenantId, ftsEnabled);
			List<Content> listByDataClass = findAllContentByDataClass(paramMap, tenantId, ftsEnabled);
			if(listByKeyword != null) {
				list.addAll(listByKeyword);
			}
			if(listByDataClass != null) {
				list.addAll(listByDataClass);
			}
		}
		return list;
	}
	
	public List<Content> findAllContentByKeyword(Map<String, Set<Object>> paramMap, String tenantId, boolean ftsEnabled) throws ParseException {
		boolean keywordFilterExists = false;
		Query query = new Query();
		List<Criteria> andCriteriaList = new ArrayList<Criteria>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		for (Map.Entry<String, Set<Object>> entry : paramMap.entrySet()) {
			if ("creationDateTime".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					Iterator<Object> itr = entry.getValue().iterator();
					Date from = sdf.parse(itr.next().toString());
					Date to = sdf.parse(itr.next().toString());
					Date tmp = from;
					if(from.after(to)) {
						from = to;
						to = tmp;
					}
					andCriteriaList.add(Criteria.where(entry.getKey()).gte(from));
					andCriteriaList.add(Criteria.where(entry.getKey()).lte(to));
					continue;
				}
				continue;
			} else if ("revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					Iterator<Object> itr = entry.getValue().iterator();
					Date from = sdf.parse(itr.next().toString());
					Date to = sdf.parse(itr.next().toString());
					Date tmp = from;
					if(from.after(to)) {
						from = to;
						to = tmp;
					}
					andCriteriaList.add(Criteria.where(entry.getKey()).gte(from));
					andCriteriaList.add(Criteria.where(entry.getKey()).lte(to));
					continue;
				}
				continue;
			} else if ("name".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					String value = entry.getValue().iterator().next().toString();
					if(!StringUtils.isEmpty(value)) {
						andCriteriaList.add(Criteria.where("metadata.keywords").regex(value,"i"));
						keywordFilterExists = true;
					}
				}
				continue;
			}
		}
		if(!keywordFilterExists) {
			return null;
		}
		andCriteriaList.add(Criteria.where(Content.DELETED_PARAM).ne("true"));
		andCriteriaList.add(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(new Criteria().andOperator(andCriteriaList.toArray(new Criteria[andCriteriaList.size()])));
		
		List<Content> list = null;
		try {
			list = mongoTemplate.find(query, Content.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return list;
	}
	
	public List<Content> findAllContentByDataClass(Map<String, Set<Object>> paramMap, String tenantId, boolean ftsEnabled) throws ParseException {
		boolean dataClassFilterExists = false;
		Query query = new Query();
		List<Criteria> andCriteriaList = new ArrayList<Criteria>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		for (Map.Entry<String, Set<Object>> entry : paramMap.entrySet()) {
			if ("creationDateTime".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					Iterator<Object> itr = entry.getValue().iterator();
					Date from = sdf.parse(itr.next().toString());
					Date to = sdf.parse(itr.next().toString());
					Date tmp = from;
					if(from.after(to)) {
						from = to;
						to = tmp;
					}
					andCriteriaList.add(Criteria.where(entry.getKey()).gte(from));
					andCriteriaList.add(Criteria.where(entry.getKey()).lte(to));
					continue;
				}
				continue;
			} else if ("revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					Iterator<Object> itr = entry.getValue().iterator();
					Date from = sdf.parse(itr.next().toString());
					Date to = sdf.parse(itr.next().toString());
					Date tmp = from;
					if(from.after(to)) {
						from = to;
						to = tmp;
					}
					andCriteriaList.add(Criteria.where(entry.getKey()).gte(from));
					andCriteriaList.add(Criteria.where(entry.getKey()).lte(to));
					continue;
				}
				continue;
			} else if ("name".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					String value = entry.getValue().iterator().next().toString();
					if(!StringUtils.isEmpty(value)) {
						andCriteriaList.add(Criteria.where("dataClassText").regex(value,"i"));
						dataClassFilterExists = true;
					}
				}
				continue;
			}
		}
		if(!dataClassFilterExists) {
			return null;
		}
		andCriteriaList.add(Criteria.where(Content.DELETED_PARAM).ne("true"));
		andCriteriaList.add(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(new Criteria().andOperator(andCriteriaList.toArray(new Criteria[andCriteriaList.size()])));
		
		List<Content> list = null;
		try {
			list = mongoTemplate.find(query, Content.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Folder> findAllFolders(Map<String, Set<Object>> paramMap,String tenantId) throws ParseException {
		boolean nameFilterExists = false;
		Query query = new Query();
		List<Criteria> andCriteriaList = new ArrayList<Criteria>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		for (Map.Entry<String, Set<Object>> entry : paramMap.entrySet()) {
			if ("creationDateTime".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					Iterator<Object> itr = entry.getValue().iterator();
					Date from = sdf.parse(itr.next().toString());
					Date to = sdf.parse(itr.next().toString());
					Date tmp = from;
					if(from.after(to)) {
						from = to;
						to = tmp;
					}
					andCriteriaList.add(Criteria.where(entry.getKey()).gte(from));
					andCriteriaList.add(Criteria.where(entry.getKey()).lte(to));
					continue;
				}
				continue;
			} else if ("name".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					String value = entry.getValue().iterator().next().toString();
					if(!StringUtils.isEmpty(value)) {
						List<String> systemFolderNames = new ArrayList<String>();
						systemFolderNames.add("TenantAssetFolder");
						systemFolderNames.add("TenantActivityFolder");
						systemFolderNames.add("TenantCasesFolder");
						systemFolderNames.add("TenantCaseTypesFolder");
						systemFolderNames.add("TenantTempFolder");
						nameFilterExists = true;
						andCriteriaList.add(Criteria.where("folderName").regex(value,"i"));
						andCriteriaList.add(Criteria.where("folderName").nin(systemFolderNames));
					}
				}
				continue;
			}
		}
		if(!nameFilterExists) {
			return null;
		}
		andCriteriaList.add(Criteria.where(Content.DELETED_PARAM).ne("true"));
		andCriteriaList.add(Criteria.where("tenantId").is(tenantId));
		andCriteriaList.add(Criteria.where("folderType").is("folder"));
		query.addCriteria(new Criteria().andOperator(andCriteriaList.toArray(new Criteria[andCriteriaList.size()])));
		
		
		List<Folder> list = null;
		try {
			list = mongoTemplate.find(query, Folder.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		List<Folder> listByKeyword = findAllFolderByKeyword(paramMap, tenantId);
		List<Folder> listByDataClass = findAllFolderByDataClass(paramMap, tenantId);
		if(listByKeyword != null) {
			list.addAll(listByKeyword);
		}
		if(listByDataClass != null) {
			list.addAll(listByDataClass);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Folder> findAllFolderByKeyword(Map<String, Set<Object>> paramMap,String tenantId) throws ParseException {
		boolean nameFilterExists = false;
		Query query = new Query();
		List<Criteria> andCriteriaList = new ArrayList<Criteria>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		for (Map.Entry<String, Set<Object>> entry : paramMap.entrySet()) {
			if ("creationDateTime".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					Iterator<Object> itr = entry.getValue().iterator();
					Date from = sdf.parse(itr.next().toString());
					Date to = sdf.parse(itr.next().toString());
					Date tmp = from;
					if(from.after(to)) {
						from = to;
						to = tmp;
					}
					andCriteriaList.add(Criteria.where(entry.getKey()).gte(from));
					andCriteriaList.add(Criteria.where(entry.getKey()).lte(to));
					continue;
				}
				continue;
			} else if ("name".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					String value = entry.getValue().iterator().next().toString();
					if(!StringUtils.isEmpty(value)) {
						List<String> systemFolderNames = new ArrayList<String>();
						systemFolderNames.add("TenantAssetFolder");
						systemFolderNames.add("TenantActivityFolder");
						systemFolderNames.add("TenantCasesFolder");
						systemFolderNames.add("TenantCaseTypesFolder");
						systemFolderNames.add("TenantTempFolder");
						nameFilterExists = true;
						andCriteriaList.add(Criteria.where("metadata.keywords").regex(value,"i"));
						andCriteriaList.add(Criteria.where("folderName").nin(systemFolderNames));
					}
				}
				continue;
			}
		}
		if(!nameFilterExists) {
			return null;
		}
		andCriteriaList.add(Criteria.where(Content.DELETED_PARAM).ne("true"));
		andCriteriaList.add(Criteria.where("tenantId").is(tenantId));
		andCriteriaList.add(Criteria.where("folderType").is("folder"));
		query.addCriteria(new Criteria().andOperator(andCriteriaList.toArray(new Criteria[andCriteriaList.size()])));
		return mongoTemplate.find(query, Folder.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<Folder> findAllFolderByDataClass(Map<String, Set<Object>> paramMap,String tenantId) throws ParseException {
		boolean nameFilterExists = false;
		Query query = new Query();
		List<Criteria> andCriteriaList = new ArrayList<Criteria>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		for (Map.Entry<String, Set<Object>> entry : paramMap.entrySet()) {
			if ("creationDateTime".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					Iterator<Object> itr = entry.getValue().iterator();
					Date from = sdf.parse(itr.next().toString());
					Date to = sdf.parse(itr.next().toString());
					Date tmp = from;
					if(from.after(to)) {
						from = to;
						to = tmp;
					}
					andCriteriaList.add(Criteria.where(entry.getKey()).gte(from));
					andCriteriaList.add(Criteria.where(entry.getKey()).lte(to));
					continue;
				}
				continue;
			} else if ("name".equalsIgnoreCase(entry.getKey())) {
				if(entry.getValue().size() > 0) {
					String value = entry.getValue().iterator().next().toString();
					if(!StringUtils.isEmpty(value)) {
						List<String> systemFolderNames = new ArrayList<String>();
						systemFolderNames.add("TenantAssetFolder");
						systemFolderNames.add("TenantActivityFolder");
						systemFolderNames.add("TenantCasesFolder");
						systemFolderNames.add("TenantCaseTypesFolder");
						systemFolderNames.add("TenantTempFolder");
						nameFilterExists = true;
						andCriteriaList.add(Criteria.where("dataClassText").regex(value,"i"));
						andCriteriaList.add(Criteria.where("folderName").nin(systemFolderNames));
					}
				}
				continue;
			}
		}
		if(!nameFilterExists) {
			return null;
		}
		andCriteriaList.add(Criteria.where(Content.DELETED_PARAM).ne("true"));
		andCriteriaList.add(Criteria.where("tenantId").is(tenantId));
		andCriteriaList.add(Criteria.where("folderType").is("folder"));
		query.addCriteria(new Criteria().andOperator(andCriteriaList.toArray(new Criteria[andCriteriaList.size()])));
		return mongoTemplate.find(query, Folder.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<Folder> findAllFolders(List<String> folderIds, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("id").in(folderIds));
		return mongoTemplate.find(query, Folder.class);
	}

	@SuppressWarnings("unchecked")
	public Content getContentByRevisedDateTime(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("parentFolderId").is(id));
		// query.addCriteria(Criteria.where("contentType").is("content"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.with(new Sort(Sort.Direction.DESC, "revisedDateTime"));
		List<Content> contents = null;
		Content content = null;
		try {
			contents = mongoTemplate.find(query, Content.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (contents == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				contents = mongoTemplate.find(query, Content.class);
			}
			logger.debug(" " + contents);
		}
		if (contents.size() > 0)
			content = contents.get(0);
		return content;
	}

}
