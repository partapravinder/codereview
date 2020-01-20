package com.newgen.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.newgen.dao.StorageCredentialDao;
import com.newgen.model.StorageCredentials;
import com.newgen.repository.StorageCredentialRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class StorageCredentialDaoImpl implements StorageCredentialDao {
	private static final Logger logger = LoggerFactory.getLogger(StorageCredentialDaoImpl.class);
	@SuppressWarnings("rawtypes")
	@Autowired
	WrapperMongoService mongoTemplate;

	@Autowired
	StorageCredentialRepository storageCredentialRepository;

	@Override
	public StorageCredentials insert(StorageCredentials storageCredentials,String tenantId) {
		try {
			return storageCredentialRepository.insert(storageCredentials);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return storageCredentialRepository.insert(storageCredentials);
        }
	}

	@Override
	public StorageCredentials findById(String id,String tenantId) {
		StorageCredentials storageCredentials = null;
		Optional<StorageCredentials> dbStorageCredentials;
		try {
			dbStorageCredentials = storageCredentialRepository.findById(id);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	dbStorageCredentials = storageCredentialRepository.findById(id);
        }
		if(dbStorageCredentials.isPresent()) {
			//operate on existing contentLocation
			storageCredentials = dbStorageCredentials.get();
		} else {
		    //there is no Customer in the repo with 'id'
		}
		return storageCredentials;
	}

	@Override
	public StorageCredentials findAndRemoveById(String id,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		@SuppressWarnings("unchecked")
		StorageCredentials storageCredentials = (StorageCredentials) mongoTemplate.findAndRemove(query,
				StorageCredentials.class);
		logger.debug("Deleted Storage Credentials : " + storageCredentials);
		return storageCredentials;
	}

	@Override
	public StorageCredentials findAndRemoveByIdAndVersion(String id, String version,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(
				Criteria.where("_id").is(id).andOperator(Criteria.where("version").is(Long.valueOf(version))));
		@SuppressWarnings("unchecked")
		StorageCredentials storageCredentials = (StorageCredentials) mongoTemplate.findAndRemove(query,
				StorageCredentials.class);
		logger.debug("Deleted Storage Credentials : " + storageCredentials);
		return storageCredentials;
	}

	@Override
	public StorageCredentials findAndModify(String id, Map<String, String> updateParams, Long version,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		if (version == null) {
			query.addCriteria(Criteria.where("_id").is(id));
		} else {
			query.addCriteria(Criteria.where("_id").is(id).andOperator(Criteria.where("version").is(version)));
		}

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		for (Map.Entry<String, String> entry : updateParams.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
					|| "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			update.set(entry.getKey(), entry.getValue());
		}

		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		@SuppressWarnings("unchecked")
		StorageCredentials storageCredentials = (StorageCredentials) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), StorageCredentials.class);
		logger.debug("Updated StorageCredentials - " + storageCredentials);
		return storageCredentials;
	}

	@Override
	public List<StorageCredentials> findAll() {
		try {
			return storageCredentialRepository.findAll();
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return storageCredentialRepository.findAll();
        }
	}

}
