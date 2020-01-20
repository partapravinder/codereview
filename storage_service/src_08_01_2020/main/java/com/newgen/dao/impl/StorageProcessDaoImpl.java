package com.newgen.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.DBRef;
import com.newgen.dao.StorageProcessDao; 
import com.newgen.model.StorageProcess;
import com.newgen.model.StorageProcess.Status;
import com.newgen.repository.StorageProcessRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class StorageProcessDaoImpl implements StorageProcessDao {
	
	private static final Logger logger = LoggerFactory.getLogger(StorageProcessDaoImpl.class);
	
	@SuppressWarnings("rawtypes")
	@Autowired
	WrapperMongoService mongoTemplate;

	@Autowired
	StorageProcessRepository storageProcessRepository;

	@Override
	public StorageProcess insert(StorageProcess storageProcess,String tenantId) {
		try {
			return storageProcessRepository.insert(storageProcess);	
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return storageProcessRepository.insert(storageProcess);
        }
	}

	@Override
	public StorageProcess findById(String id) {
		StorageProcess storageProcess = null;
		Optional<StorageProcess> dbStorageProcess;
		try {
			dbStorageProcess = storageProcessRepository.findById(id);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	dbStorageProcess = storageProcessRepository.findById(id);
        }
		if(dbStorageProcess.isPresent()) {
			//operate on existing contentLocation
			storageProcess = dbStorageProcess.get();
		} else {
		    //there is no Customer in the repo with 'id'
		}
		return storageProcess;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public StorageProcess findStatusById(String id,String tenantId) {
		  
		 
			Query query = new Query();
			query.addCriteria(Criteria.where("id").is(id));

			query.addCriteria(Criteria.where("tenantId").is(tenantId));
			StorageProcess storageProcess = (StorageProcess) mongoTemplate.findOne(query, StorageProcess.class);
		//	logger.debug("Found folder : " + folder);
			return storageProcess;
		}
//		
//		Optional<StorageProcess> dbStorageProcess = storageProcessRepository.findById(id);
//		if(dbStorageProcess.isPresent()) {
//			//operate on existing contentLocation
//			storageProcess = dbStorageProcess.get();
//		} else {
//		    //there is no Customer in the repo with 'id'
//		}
//		return storageProcess;
	//}
	

	@Override
	public StorageProcess updateStatus(String id, Status status, String storageLocationId,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		Update update = new Update();
		update.set("status", status);
		update.set("statusChangeDateTime", new Date());
		if (storageLocationId != null && !storageLocationId.isEmpty()) {
			DBRef storageLocationRef = new DBRef("storageLocation", storageLocationId);
			update.set("storageLocation", storageLocationRef);
		}

		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		@SuppressWarnings("unchecked")
		StorageProcess storageProcess = (StorageProcess) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), StorageProcess.class);
		logger.debug("Updated Storage Process - " + storageProcess);
		return storageProcess;
	}

	@Override
	public StorageProcess findAndRemoveById(String id,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		@SuppressWarnings("unchecked")
		StorageProcess storageProcess = (StorageProcess) mongoTemplate.findAndRemove(query, StorageProcess.class);
		logger.debug("Deleted Storage Process : " + storageProcess);
		return storageProcess;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StorageProcess> findAllAcknowldegedAndFailed() {
		Query query = new Query();
	//	query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Date compareDate = DateUtils.addMinutes(new Date(), -5);
//		query.addCriteria((Criteria.where("status").is(Status.ACKNOWLEDGED)).orOperator(Criteria.where("status").is(Status.FAILED))
//				.andOperator(Criteria.where("statusChangeDateTime").lte(compareDate)));
	
	
		query.addCriteria(new Criteria().orOperator(Criteria.where("status").is(Status.ACKNOWLEDGED),
				Criteria.where("status").is(Status.FAILED))	);
		query.addCriteria(Criteria.where("statusChangeDateTime").lte(compareDate));
		return mongoTemplate.find(query, StorageProcess.class);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StorageProcess> findAllCompletedForDays(int days) {
		Query query = new Query();
//		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Date compareDate = DateUtils.addDays(new Date(), -days);
		query.addCriteria(Criteria.where("status").is(Status.COMPLETED)
				.andOperator(Criteria.where("statusChangeDateTime").lte(compareDate)));
		return mongoTemplate.find(query, StorageProcess.class);
	}
}
