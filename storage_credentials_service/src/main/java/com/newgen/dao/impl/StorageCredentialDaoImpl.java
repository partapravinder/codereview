package com.newgen.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
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
	public StorageCredentials insert(StorageCredentials storageCredentials, String tenantId) {
		StorageCredentials storageCredentials2 = null;
		try {
			storageCredentials2 = storageCredentialRepository.insert(storageCredentials);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (storageCredentials2 == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				storageCredentials2 = storageCredentialRepository.insert(storageCredentials);
			}
			logger.debug(" " + storageCredentials2);
		}
		return storageCredentials2;
	}

	@Override
	public StorageCredentials findById(String id, String tenantId) {
		StorageCredentials storageCredentials = null;
		Optional<StorageCredentials> dbStorageCredentials = null;
		try {
			dbStorageCredentials = storageCredentialRepository.findById(id);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (dbStorageCredentials == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				dbStorageCredentials = storageCredentialRepository.findById(id);
			}
			logger.debug(" " + dbStorageCredentials);
		}
		if (dbStorageCredentials.isPresent()) {
			// operate on existing contentLocation
			storageCredentials = dbStorageCredentials.get();
		} else {
			// there is no Customer in the repo with 'id'
		}
		return storageCredentials;
	}

	@SuppressWarnings("unchecked")
	@Override
	public StorageCredentials findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		StorageCredentials storageCredentials = null;
		try {
			storageCredentials = (StorageCredentials) mongoTemplate.findAndRemove(query, StorageCredentials.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (storageCredentials == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				storageCredentials = (StorageCredentials) mongoTemplate.findAndRemove(query, StorageCredentials.class);
			}
			logger.debug(" " + storageCredentials);
		}
		logger.debug("Deleted Storage Credentials : " + storageCredentials);
		return storageCredentials;
	}

	@SuppressWarnings("unchecked")
	@Override
	public StorageCredentials findAndRemoveByIdAndVersion(String id, String version, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(
				Criteria.where("_id").is(id).andOperator(Criteria.where("version").is(Long.valueOf(version))));
		StorageCredentials storageCredentials = null;
		try {
			storageCredentials = (StorageCredentials) mongoTemplate.findAndRemove(query, StorageCredentials.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (storageCredentials == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				storageCredentials = (StorageCredentials) mongoTemplate.findAndRemove(query, StorageCredentials.class);
			}
			logger.debug(" " + storageCredentials);
		}
		logger.debug("Deleted Storage Credentials : " + storageCredentials);
		return storageCredentials;
	}

	@SuppressWarnings("unchecked")
	@Override
	public StorageCredentials findAndModify(String id, Map<String, String> updateParams, Long version,
			String tenantId) {
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
		StorageCredentials storageCredentials = null;
		try {
			storageCredentials = (StorageCredentials) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), StorageCredentials.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (storageCredentials == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				storageCredentials = (StorageCredentials) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), StorageCredentials.class);
			}
			logger.debug(" " + storageCredentials);
		}
		logger.debug("Updated StorageCredentials - " + storageCredentials);
		return storageCredentials;
	}

	@Override
	public List<StorageCredentials> findAll() {
		return storageCredentialRepository.findAll();
	}

}
