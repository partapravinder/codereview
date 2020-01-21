package com.newgen.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.newgen.dao.StorageLocationDao;
import com.newgen.model.StorageLocation;
import com.newgen.repository.StorageLocationRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class StorageLocationDaoImpl implements StorageLocationDao {
	private static final Logger logger = LoggerFactory.getLogger(StorageLocationDaoImpl.class);
	@SuppressWarnings("rawtypes")
	@Autowired
	WrapperMongoService mongoTemplate;

	@Autowired
	StorageLocationRepository storageLocationRepository;

	@Override
	public StorageLocation insert(StorageLocation storageLocation, String tenantId) {
		try {
			return storageLocationRepository.insert(storageLocation);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return storageLocationRepository.insert(storageLocation);
        }
	}

	@Override
	public StorageLocation findById(String id, String tenantId) {
		StorageLocation storageLocation = null;
		Optional<StorageLocation> dbStorageLocation;
		try {
			dbStorageLocation = storageLocationRepository.findById(id);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	dbStorageLocation = storageLocationRepository.findById(id);
        }
		if (dbStorageLocation.isPresent()) {
			// operate on existing contentLocation
			storageLocation = dbStorageLocation.get();
		} else {
			// there is no Customer in the repo with 'id'
		}
		return storageLocation;
	}

	public List<StorageLocation> findByBlobUris(List<String> ids, String tenantId) {

		List<StorageLocation> list = new ArrayList<StorageLocation>();
		ids.stream().forEach(blobUri -> {
			Query query = new Query();
			query.addCriteria(Criteria.where("blobUri").is(blobUri));
			query.addCriteria(Criteria.where("tenantId").is(tenantId));
			StorageLocation storageLocation = (StorageLocation) mongoTemplate.findOne(query, StorageLocation.class);
			if (storageLocation != null) {
				if (storageLocation.getId() != null){
					list.add(storageLocation);
				}
			}
			
		});
		return list;
	}

	@Override
	public StorageLocation findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		@SuppressWarnings("unchecked")
		StorageLocation storageLocation = (StorageLocation) mongoTemplate.findAndRemove(query, StorageLocation.class);
		logger.debug("Deleted Storage Location : " + storageLocation.toString());
		return storageLocation;
	}

	@Override
	public StorageLocation findAndRemoveByIdAndVersion(String id, String version, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(
				Criteria.where("_id").is(id).andOperator(Criteria.where("version").is(Long.valueOf(version))));
		@SuppressWarnings("unchecked")

		StorageLocation storageLocation = (StorageLocation) mongoTemplate.findAndRemove(query, StorageLocation.class);
		logger.debug("Deleted Storage Location : " + storageLocation.toString());
		return storageLocation;
	}

}
