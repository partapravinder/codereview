package com.newgen.dao.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.constants.Constants;
import com.newgen.dao.FolderDao;
import com.newgen.model.Folder;
import com.newgen.model.InOutParameters;
import com.newgen.repository.FolderRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class FolderDaoImpl implements FolderDao, Constants {
	private static final Logger logger = LoggerFactory.getLogger(FolderDaoImpl.class);
	@SuppressWarnings("rawtypes")
	@Autowired
	WrapperMongoService mongoTemplate;

	@Autowired
	FolderRepository folderRepository;

	@Value("${pagination.batchSize}")
	int pagesize;

	public static final String DELETED_PARAM = "deleted";

	@Override
	public Folder insert(Folder folder) {
		logger.debug("--------- " + folder.getMetadata());
		Folder f = null;
		try {
			f = folderRepository.insert(folder);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (f == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				f = folderRepository.insert(folder);
			}
			logger.debug(" " + f);
		}
		return f;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InOutParameters findById(String id, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
		query.addCriteria(Criteria.where("id").is(id));
		// .andOperator(Criteria.where(DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		// query.addCriteria(Criteria.where("folderType").is("folder"));
		Folder folder = null;
		try {
			folder = (Folder) mongoTemplate.findOne(query, Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (folder == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				folder = (Folder) mongoTemplate.findOne(query, Folder.class);
			}
			logger.debug(" " + folder);
		}
		logger.debug("Found folder : " + folder);
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(folder).totalSize()) / 1024.0);
		inOutParameters.setFolder(folder);
		return inOutParameters;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Folder findCabinetById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));

		// .andOperator(Criteria.where(DELETED_PARAM).ne("true")));

		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		// query.addCriteria(Criteria.where("folderType").is("cabinet"));
		Folder folder = null;
		try {
			folder = (Folder) mongoTemplate.findOne(query, Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (folder == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				folder = (Folder) mongoTemplate.findOne(query, Folder.class);
			}
			logger.debug(" " + folder);
		}

		logger.debug("Found Cabinet : " + folder);
		return folder;
	}

	@SuppressWarnings("unchecked")
	public Folder findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		// query.addCriteria(Criteria.where("folderType").is("folder"));
		logger.debug("Deleting folder : " + id);
		Folder folder = null;
		try {
			folder = (Folder) mongoTemplate.findAndRemove(query, Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (folder == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				folder = (Folder) mongoTemplate.findAndRemove(query, Folder.class);
			}
			logger.debug(" " + folder);
		}
		logger.debug("Deleted folder : " + folder);
		return folder;
	}

	@SuppressWarnings("unchecked")
	public Folder findAndRemoveByIdAndVersion(String id, String version, String tenantId) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("id").is(id).andOperator(Criteria.where(VERSION_PARAM).is(Long.valueOf(version))));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Folder folder = null;
		try {
			folder = (Folder) mongoTemplate.findAndRemove(query, Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (folder == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				folder = (Folder) mongoTemplate.findAndRemove(query, Folder.class);
			}
			logger.debug(" " + folder);
		}
		logger.debug("Deleted folder : " + folder);
		return folder;
	}

	@SuppressWarnings("unchecked")
	public List<Folder> findByParentFolderId(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("parentFolderId").is(id));
		// .andOperator(Criteria.where(DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("folderType").is("folder"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		List<Folder> folders = null;
		try {
			folders = mongoTemplate.find(query, Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (folders == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				folders = mongoTemplate.find(query, Folder.class);
			}
			logger.debug(" " + folders);
		}
		return folders;
	}

	@SuppressWarnings("unchecked")
	public List<Folder> findAllFolders(Map<String, String[]> paramMap, String tenantId) {
		Query query = new Query();
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			if ("parentFolderId".equalsIgnoreCase(entry.getKey())) {
				query.addCriteria(Criteria.where("parentFolderId").is(entry.getValue()[0]));
				logger.debug("parentFolderId=> " + entry.getValue()[0]);
				continue;
			}
			try {
				logger.debug(entry.getKey() + "=>" + URLDecoder.decode(entry.getValue()[0], "UTF-8"));
				if ("folderName".equalsIgnoreCase(entry.getKey()) && entry.getValue()[0].toString().contains("*")) {
					query.addCriteria(Criteria.where(entry.getKey()).regex(toLikeRegex(entry.getValue()[0]), "i"));
				} else {
					query.addCriteria(
							Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0], "UTF-8")));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		query.addCriteria(Criteria.where("folderType").is("folder"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		List<Folder> folders = null;
		try {
			folders = mongoTemplate.find(query, Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (folders == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				folders = mongoTemplate.find(query, Folder.class);
			}
			logger.debug(" " + folders);
		}
		return folders;
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
	public List<Folder> findAllFoldersByPage(Map<String, String[]> paramMap, String tenantId, int Pno) {
		Query query = new Query();
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			try {
				logger.debug(entry.getKey() + "=>" + URLDecoder.decode(entry.getValue()[0], "UTF-8"));
				query.addCriteria(Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0], "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (Pno >= 0) {
			final Pageable pageableRequest = PageRequest.of(Pno, pagesize);
			query.with(pageableRequest);
		}
		query.addCriteria(Criteria.where("folderType").is("folder"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		List<Folder> folders = null;
		try {
			folders = mongoTemplate.find(query, Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (folders == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				folders = mongoTemplate.find(query, Folder.class);
			}
			logger.debug(" " + folders);
		}
		return folders;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Folder findAndModify(String id, String updateFolderParams, Long version, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		if (version == null) {
			query.addCriteria(Criteria.where("_id").is(id));
		} else {
			query.addCriteria(Criteria.where("_id").is(id).andOperator(Criteria.where(VERSION_PARAM).is(version)));
		}
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		if (updateFolderParams == null) {
			update.unset("metadata");
		} else {
			JSONObject updatedParamsJson = new JSONObject(updateFolderParams);
			ObjectMapper obj_mapper = new ObjectMapper();

			// Handle metadata
			if (updatedParamsJson.has("metadata") && updatedParamsJson.get("metadata") != null) {
				Map<String, String> metamap = handleMetadata(updatedParamsJson.toString(), id, tenantId);
				update.set("metadata", metamap);
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
						|| "folderType".equalsIgnoreCase(entry.getKey())
						|| "creationDateTime".equalsIgnoreCase(entry.getKey())
						|| "accessDateTime".equalsIgnoreCase(entry.getKey())
						|| "revisedDateTime".equalsIgnoreCase(entry.getKey())
						|| "parentFolder".equalsIgnoreCase(entry.getKey())
						|| "ownerName".equalsIgnoreCase(entry.getKey()) || "ownerId".equalsIgnoreCase(entry.getKey())) {
					continue;
				}
				update.set(entry.getKey(), entry.getValue());
			}
		}

		Folder updatedFolder = null;
		try {
			updatedFolder = (Folder) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (updatedFolder == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				updatedFolder = (Folder) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), Folder.class);
			}
			logger.debug(" " + updatedFolder);
		}
		logger.debug("Updated folder - " + updatedFolder);
		return updatedFolder;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> handleMetadata(String updateFolderParams, String id, String tenantId) {
		JSONObject updatedParamsJson = new JSONObject(updateFolderParams);
		ObjectMapper obj_mapper = new ObjectMapper();
		Map<String, String> metamap = null;
		try {
			metamap = obj_mapper.readValue(updatedParamsJson.get("metadata").toString(), Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get existing metadata map
		Folder folder_temp = findById(id, tenantId).getFolder();
		if (folder_temp.getMetadata() != null) {// If already metadata exists, append new map
			Map<String, String> Existing_metamap = folder_temp.getMetadata();
			Existing_metamap.putAll(metamap);
			return Existing_metamap;
			// update.set("metadata",Existing_metamap);
		} else {// add new map metadata
			return metamap;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Folder> findByFolderName(String folderName, String tenantId) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("folderName").regex(folderName).andOperator(Criteria.where(DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("folderType").is("folder"));
		List<Folder> folders = null;
		try {
			folders = mongoTemplate.find(query, Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (folders == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				folders = mongoTemplate.find(query, Folder.class);
			}
			logger.debug(" " + folders);
		}
		return folders;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Folder updateParentFolderId(String id, String targetId, Long version, String tenantId) {
		Query query = new Query();
		if (version == null) {
			query.addCriteria(Criteria.where("id").is(id));
		} else {
			query.addCriteria(Criteria.where("id").is(id).andOperator(Criteria.where(VERSION_PARAM).is(version)));
		}
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("folderType").is("folder"));
		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());
		update.set("parentFolderId", targetId);

		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		Folder movedFolder = null;
		try {
			movedFolder = (Folder) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (movedFolder == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				movedFolder = (Folder) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), Folder.class);
			}
			logger.debug(" " + movedFolder);
		}
		logger.debug("Moved folder - " + movedFolder);
		return movedFolder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Folder updateChildrenCount(String id, int count, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());
		update.set("stats.childrenCount", count);

		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		Folder updatedFolder = null;
		try {
			updatedFolder = (Folder) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (updatedFolder == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				updatedFolder = (Folder) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), Folder.class);
			}
			logger.debug(" " + updatedFolder);
		}
		logger.debug("Folder stats updated - " + updatedFolder);
		return updatedFolder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Folder> findAllContentsByMetadata(String searchParams, String tenantId) {
		Query query = new Query();
		JSONObject paramJson = new JSONObject(searchParams);
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

		List<Folder> folders = null;
		try {
			folders = mongoTemplate.find(query, Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (folders == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				folders = mongoTemplate.find(query, Folder.class);
			}
			logger.debug(" " + folders);
		}
		return folders;
	}

	@SuppressWarnings("unchecked")
	public List<Folder> findByParentFolderIdInGroup(List<String> userIds) {
		Query query = new Query();
		query.addCriteria(Criteria.where("ownerId").in(userIds));
		query.addCriteria(Criteria.where("folderType").is("folder"));
		query.addCriteria(Criteria.where("isPrivate").is(false));
		List<Folder> folders = null;
		try {
			folders = mongoTemplate.find(query, Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (folders == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				folders = mongoTemplate.find(query, Folder.class);
			}
			logger.debug(" " + folders);
		}
		return folders;
	}

	@SuppressWarnings("unchecked")
	public List<Folder> findByPrivateFolderId(List<String> folderIds) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").in(folderIds));
		query.addCriteria(Criteria.where("folderType").is("folder"));
		List<Folder> folders = null;
		try {
			folders = mongoTemplate.find(query, Folder.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (folders == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				folders = mongoTemplate.find(query, Folder.class);
			}
			logger.debug(" " + folders);
		}
		return folders;
	}

}
