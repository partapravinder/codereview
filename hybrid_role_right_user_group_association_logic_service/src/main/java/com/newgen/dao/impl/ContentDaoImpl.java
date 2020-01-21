package com.newgen.dao.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.dao.ContentDao;
import com.newgen.model.Content;
import com.newgen.model.InOutParameters;
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

	public static final String VERSION_PARAM = "version";

	public static final String DELETED_PARAM = "deleted";

	public static final String REVISEDDATETIME_PARAM = "revisedDateTime";
	public static final String ACCESSDATETIME_PARAM = "accessDateTime";

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
		Content content = null;
		try {
			content = (Content) mongoTemplate.findAndRemove(query, Content.class);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			content = (Content) mongoTemplate.findAndRemove(query, Content.class);
		}
		logger.debug("Deleted Content : " + content);
		return content;
	}

	@SuppressWarnings("unchecked")
	public Content findAndRemoveByIdAndVersion(String id, String version, String tenantId) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("_id").is(id).andOperator(Criteria.where(VERSION_PARAM).is(Long.valueOf(version))));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		Content content = null;
		try {
			content = (Content) mongoTemplate.findAndRemove(query, Content.class);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			content = (Content) mongoTemplate.findAndRemove(query, Content.class);
		}
		logger.debug("Deleted Content : " + content);
		return content;
	}

	@SuppressWarnings("unchecked")
	public InOutParameters findOne(String id, String tenantId) {
		Query query = new Query();
		Content content = null;
		InOutParameters inOutParameters = new InOutParameters();
		query.addCriteria(Criteria.where("_id").is(id));// .andOperator(Criteria.where(DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		try {
			content = (Content) mongoTemplate.findOne(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			content = (Content) mongoTemplate.findOne(query, Content.class);
		}
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(content).totalSize()) / 1024.0);
		inOutParameters.setContent(content);
		return inOutParameters;
	}

	@SuppressWarnings("unchecked")
	public Content findUncommitted(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id).andOperator(Criteria.where(DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		try {
			return (Content) mongoTemplate.findOne(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return (Content) mongoTemplate.findOne(query, Content.class);
		}
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
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		if (!ignoreFlag)
			query.addCriteria(Criteria.where("flag").is("COMMITTED"));

		try {
			return mongoTemplate.find(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return mongoTemplate.find(query, Content.class);
		}
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

		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		if (!ignoreFlag)
			query.addCriteria(Criteria.where("flag").is("COMMITTED"));

		try {
			return mongoTemplate.find(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return mongoTemplate.find(query, Content.class);
		}
	}

	@Override
	public Content findAndModify(String id, String updateContentParams, Long version, String tenantId) {
		return findAndModify(id, updateContentParams, version, false, tenantId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Content findAndModify(String id, String updateContentParams, Long version, boolean ignoreCommittedFlag,
			String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		if (version == null) {
			query.addCriteria(Criteria.where("_id").is(id));
		} else {
			query.addCriteria(Criteria.where("_id").is(id).andOperator(Criteria.where(VERSION_PARAM).is(version)));
		}
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		if (!ignoreCommittedFlag)
			query.addCriteria(Criteria.where("flag").is("COMMITTED"));

		Update update = new Update();
		update.set(REVISEDDATETIME_PARAM, new Date());
		update.set(ACCESSDATETIME_PARAM, new Date());

		if (updateContentParams == null) {
			update.unset("metadata");
		} else {
			JSONObject updatedParamsJson = new JSONObject(updateContentParams);
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
				Content content_temp = findOne(id, tenantId).getContent();
				if (content_temp.getMetadata() != null) {// If already metadata exists, append new map
					Map<String, String> Existing_metamap = content_temp.getMetadata();
					Existing_metamap.putAll(metamap);
					update.set("metadata", Existing_metamap);
				} else {// add new map metadata
					update.set("metadata", metamap);
				}
				updatedParamsJson.remove("metadata");
			}

			Map<String, String> contentMap = null;
			try {
				contentMap = obj_mapper.readValue(updatedParamsJson.toString(), Map.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (Map.Entry<String, String> entry : contentMap.entrySet()) {
				if ("id".equalsIgnoreCase(entry.getKey()) || VERSION_PARAM.equalsIgnoreCase(entry.getKey())
						|| "parentFolderId".equalsIgnoreCase(entry.getKey())
						|| "contentLocationId".equalsIgnoreCase(entry.getKey())
						|| "creationDateTime".equalsIgnoreCase(entry.getKey())
						|| ACCESSDATETIME_PARAM.equalsIgnoreCase(entry.getKey())
						|| REVISEDDATETIME_PARAM.equalsIgnoreCase(entry.getKey())
						|| "parentFolder".equalsIgnoreCase(entry.getKey())
						|| "contentLocation".equalsIgnoreCase(entry.getKey())) {
					continue;
				}
				update.set(entry.getKey(), entry.getValue());
			}
		}
		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		Content updatedContent = null;
		try {
			updatedContent = (Content) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			updatedContent = (Content) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), Content.class);
		}
		logger.debug("Updated Content - " + updatedContent);
		return updatedContent;
	}

	@SuppressWarnings("unchecked")
	public List<Content> findByParentFolderId(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("parentFolderId").is(id));
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		try {
			return mongoTemplate.find(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return mongoTemplate.find(query, Content.class);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Content> findByName(String name, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("name").regex(name));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		try {
			return mongoTemplate.find(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return mongoTemplate.find(query, Content.class);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Content updateParentFolderId(String id, String targetFolderId, Long version, String tenantId) {
		Query query = new Query();
		if (version == null) {
			query.addCriteria(Criteria.where("_id").is(id));
		} else {
			query.addCriteria(Criteria.where("_id").is(id).andOperator(Criteria.where(VERSION_PARAM).is(version)));
		}
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		Update update = new Update();
		update.set(REVISEDDATETIME_PARAM, new Date());
		update.set(ACCESSDATETIME_PARAM, new Date());
		update.set("parentFolderId", targetFolderId);

		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		Content movedContent = null;
		try {
			movedContent = (Content) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			movedContent = (Content) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), Content.class);
		}
		logger.debug("Moved content - " + movedContent);
		return movedContent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Content> findAllDeletedContents() {
		Query query = new Query();
		query.addCriteria(Criteria.where(DELETED_PARAM).is("true"));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		// query.addCriteria(Criteria.where("tenantId").is(tenantId));
		try {
			return mongoTemplate.find(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return mongoTemplate.find(query, Content.class);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Content findAndRemoveContentLocation(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Update update = new Update();
		update.set(REVISEDDATETIME_PARAM, new Date());
		update.set(ACCESSDATETIME_PARAM, new Date());
		update.set("contentLocation", null);
		Content updatedContent = null;
		try {
			updatedContent = (Content) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			updatedContent = (Content) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), Content.class);
		}
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
		try {
			return (Content) mongoTemplate.findOne(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return (Content) mongoTemplate.findOne(query, Content.class);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Content findByToken(String token, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("token").is(token).andOperator(Criteria.where(DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		try {
			return (Content) mongoTemplate.findOne(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return (Content) mongoTemplate.findOne(query, Content.class);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Content findAndDeleteByToken(String token, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("token").is(token));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Content content = null;
		try {
			content = (Content) mongoTemplate.findAndRemove(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			content = (Content) mongoTemplate.findAndRemove(query, Content.class);
		}
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
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("flag").is("COMMITTED"));

		try {
			return mongoTemplate.find(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return mongoTemplate.find(query, Content.class);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Content findUncommitedOne(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id).andOperator(Criteria.where(DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		try {
			return (Content) mongoTemplate.findOne(query, Content.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return (Content) mongoTemplate.findOne(query, Content.class);
		}
	}

}
