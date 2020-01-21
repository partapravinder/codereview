package com.newgen.doa.impl;

import java.util.Date;
import java.util.HashMap;
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

import com.newgen.dao.StorageConsumptionDao;
import com.newgen.model.StorageConsumption;
import com.newgen.repository.StorageConsumptionRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class StorageConsuptionDaoImpl implements StorageConsumptionDao {
	private static final Logger logger = LoggerFactory.getLogger(StorageConsuptionDaoImpl.class);
	@SuppressWarnings("rawtypes")
	@Autowired
	WrapperMongoService mongoTemplate;

	@Autowired
	StorageConsumptionRepository storageConsumptionRepository;

	public StorageConsumption insert(StorageConsumption storageConsumption) {
		try {
			return storageConsumptionRepository.insert(storageConsumption);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return storageConsumptionRepository.insert(storageConsumption);
        }
	}

	
	public StorageConsumption findById(String id,String tenantId) {
		StorageConsumption storageConsumption = null;
		Optional<StorageConsumption> dbContentLocation;
		
		try {
			dbContentLocation = storageConsumptionRepository.findById(id);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	dbContentLocation = storageConsumptionRepository.findById(id);
        }
		if(dbContentLocation.isPresent()) {
			//operate on existing contentLocation
			storageConsumption = dbContentLocation.get();
		} else {
		    //there is no Customer in the repo with 'id'
		}
		return storageConsumption ;
	}
	
	
	@SuppressWarnings({ "unchecked", "unused" })
	public List<StorageConsumption> findAllContents(Map<String, String[]> paramMap,String tenantId) {
		Query query = new Query();
		boolean ignoreFlag = false;
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			if ("ignoreCommittedFlag".equalsIgnoreCase(entry.getKey())) {
				ignoreFlag = true;
				continue;
			}  
			query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()[0]));
		} 
		query.addCriteria(Criteria.where("tenantId").is(tenantId)); 

		return mongoTemplate.find(query, StorageConsumption.class);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<StorageConsumption> findByTenantId(String tenantId) {
		Query query = new Query(); 
		query.addCriteria(Criteria.where("tenantId").is(tenantId)); 
		return mongoTemplate.find(query, StorageConsumption.class);
	}
	
	@SuppressWarnings("unchecked")
	public StorageConsumption findByTenantIdToday(String tenantId,String date) {
		Query query = new Query();  
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("onDate").is(date));
		return (StorageConsumption) mongoTemplate.findOne(query, StorageConsumption.class);
	}
	
	public StorageConsumption findAndRemoveById(String id,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		@SuppressWarnings("unchecked")
		StorageConsumption storageConsumption = (StorageConsumption) mongoTemplate.findAndRemove(query, StorageConsumption.class);
		logger.debug("Deleted Content Location : " + storageConsumption);
		return storageConsumption;
	}

	public StorageConsumption findAndRemoveByIdAndVersion(String id, String version,String tenantId) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("_id").is(id).andOperator(Criteria.where("version").is(Long.valueOf(version))));
		@SuppressWarnings("unchecked")
		StorageConsumption storageConsumption = (StorageConsumption) mongoTemplate.findAndRemove(query, StorageConsumption.class);
		logger.debug("Deleted Content Location: " + storageConsumption);
		return storageConsumption;
	}

	public StorageConsumption findAndModify(String id, HashMap<String, Double> updateParams) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());
		for (HashMap.Entry<String, Double> entry : updateParams.entrySet()) {
			update.set(entry.getKey(), entry.getValue());
		}
		@SuppressWarnings("unchecked")
		StorageConsumption storageConsumption = (StorageConsumption) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), StorageConsumption.class);
		logger.debug("Updated ContentLocation - " + storageConsumption);
		return storageConsumption;
	}

}
