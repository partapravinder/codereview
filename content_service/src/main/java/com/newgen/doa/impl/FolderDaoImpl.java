package com.newgen.doa.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import com.newgen.constants.Constants;
import com.newgen.dao.FolderDao;
import com.newgen.model.Folder;
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
		try {
			return folderRepository.insert(folder);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return folderRepository.insert(folder);
        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public Folder findById(String id ,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id).andOperator(Criteria.where(DELETED_PARAM).ne("true")));

		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Folder folder = (Folder) mongoTemplate.findOne(query, Folder.class);
		logger.debug("Found folder : " + folder);
		return folder;
	}

	public Folder findAndRemoveById(String id,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		logger.debug("Deleting folder : " + id);
		@SuppressWarnings("unchecked")
		Folder folder = (Folder) mongoTemplate.findAndRemove(query, Folder.class);
		logger.debug("Deleted folder : " + folder);
		return folder;
	}

	public Folder findAndRemoveByIdAndVersion(String id, String version,String tenantId) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("id").is(id).andOperator(Criteria.where(VERSION_PARAM).is(Long.valueOf(version))));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		@SuppressWarnings("unchecked")
		Folder folder = (Folder) mongoTemplate.findAndRemove(query, Folder.class);
		logger.debug("Deleted folder : " + folder);
		return folder;
	}

	@SuppressWarnings("unchecked")
	public List<Folder> findByParentFolderId(String id,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("parentFolderId").is(id)
				.andOperator(Criteria.where(DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		return mongoTemplate.find(query, Folder.class);
	}

	@SuppressWarnings("unchecked")
	public List<Folder> findAllFolders(Map<String, String[]> paramMap,String tenantId) {
		Query query = new Query();
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			if ("parentFolderId".equalsIgnoreCase(entry.getKey())) {
				query.addCriteria(Criteria.where("parentFolderId").is(entry.getValue()[0]));
				logger.debug("parentFolderId=> " + entry.getValue()[0]);
				continue;
			}
			try {
				logger.debug(entry.getKey()+"=>"+URLDecoder.decode(entry.getValue()[0],"UTF-8"));
				query.addCriteria(Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0],"UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		return mongoTemplate.find(query, Folder.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<Folder> findAllFoldersByPage(Map<String, String[]> paramMap,String tenantId,int Pno) {
		Query query = new Query();
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			if ("pageno".equalsIgnoreCase(entry.getKey())) {
				
				int pno =Integer.parseInt(entry.getValue()[0]);
				
				 
				final Pageable pageableRequest = PageRequest.of(pno,pagesize);

				query.with(pageableRequest);
				//query.addCriteria(Criteria.where("parentFolderId").is(entry.getValue()[0]));
				logger.debug("parentFolderId=> " + entry.getValue()[0]);
				continue;
			}
			try {
				logger.debug(entry.getKey()+"=>"+URLDecoder.decode(entry.getValue()[0],"UTF-8"));
				query.addCriteria(Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0],"UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		query.addCriteria(Criteria.where("folderType").is("cabinet"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		return mongoTemplate.find(query, Folder.class);
	}
	
	
	

	@Override
	public Folder findAndModify(String id, Map<String, String> updateFolderParams, Long version,String tenantId) {
		Query query = new Query();
		if (version == null) {
			query.addCriteria(Criteria.where("id").is(id));
		} else {
			query.addCriteria(Criteria.where("id").is(id).andOperator(Criteria.where(VERSION_PARAM).is(version)));
		}
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		for (Map.Entry<String, String> entry : updateFolderParams.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) || VERSION_PARAM.equalsIgnoreCase(entry.getKey())
					|| "parentFolderId".equalsIgnoreCase(entry.getKey())
					|| "folderType".equalsIgnoreCase(entry.getKey())
					|| "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())
					|| "parentFolder".equalsIgnoreCase(entry.getKey()) || "ownerName".equalsIgnoreCase(entry.getKey())
					|| "ownerId".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			if("metadataId".equalsIgnoreCase(entry.getKey()) && "null".equalsIgnoreCase(entry.getValue())) {
				//update.unset("metadata");
				update.unset("metadataId");
			}
			else if("metadata".equalsIgnoreCase(entry.getKey()) && "null".equalsIgnoreCase(entry.getValue())) {
				update.unset("metadata");
			}
			else
				update.set(entry.getKey(), entry.getValue());
		}

		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		@SuppressWarnings("unchecked")
		Folder updatedFolder = (Folder) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), Folder.class);
		logger.debug("Updated folder - " + updatedFolder);
		return updatedFolder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Folder> findByFolderName(String folderName,String tenantId) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("folderName").regex(folderName).andOperator(Criteria.where(DELETED_PARAM).ne("true")));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		return mongoTemplate.find(query, Folder.class);
	}

	@Override
	public Folder updateParentFolderId(String id, String targetId, Long version,String tenantId) {
		Query query = new Query();
		if (version == null) {
			query.addCriteria(Criteria.where("id").is(id));
		} else {
			query.addCriteria(Criteria.where("id").is(id).andOperator(Criteria.where(VERSION_PARAM).is(version)));
		}
		query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());
		update.set("parentFolderId", targetId);

		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		@SuppressWarnings("unchecked")
		Folder movedFolder = (Folder) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), Folder.class);
		logger.debug("Moved folder - " + movedFolder);
		return movedFolder;
	}

	@Override
	public Folder updateChildrenCount(String id, int count,String tenantId) {
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
		@SuppressWarnings("unchecked")
		Folder updatedFolder = (Folder) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), Folder.class);
		logger.debug("Folder stats updated - " + updatedFolder);
		return updatedFolder;
	}

}
