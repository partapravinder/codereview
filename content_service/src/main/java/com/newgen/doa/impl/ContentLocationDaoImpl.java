package com.newgen.doa.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

import com.newgen.dao.ContentLocationDao;
import com.newgen.model.ContentLocation;
import com.newgen.repository.ContentLocationRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class ContentLocationDaoImpl implements ContentLocationDao {
	private static final Logger logger = LoggerFactory.getLogger(ContentLocationDaoImpl.class);
	@SuppressWarnings("rawtypes")
	@Autowired
	WrapperMongoService mongoTemplate;

	@Autowired
	ContentLocationRepository contentLocationRepository;

	public ContentLocation insert(ContentLocation contentLocation,String tenantId) {
		try {
			return contentLocationRepository.insert(contentLocation);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return contentLocationRepository.insert(contentLocation);
        }
	}

	@Override
	public ContentLocation findById(String id,String tenantId) {
		ContentLocation contentLocation = null;
		Optional<ContentLocation> dbContentLocation;
		try {
			dbContentLocation = contentLocationRepository.findById(id);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	dbContentLocation = contentLocationRepository.findById(id);
        }
		if(dbContentLocation.isPresent()) {
			//operate on existing contentLocation
			contentLocation = dbContentLocation.get();
		} else {
		    //there is no Customer in the repo with 'id'
		}
		return contentLocation ;
	}
	
	public List<ContentLocation> findByStorageLocationIds(List<String> locationIds,String tenantId) {
		List<ContentLocation> list = new ArrayList<ContentLocation>();
		locationIds.stream().forEach(locationId -> {
			Query query = new Query();
			query.addCriteria(Criteria.where("locationId").is(locationId));
			query.addCriteria(Criteria.where("tenantId").is(tenantId));
			ContentLocation contentLocation = (ContentLocation) mongoTemplate.findOne(query, ContentLocation.class);
			if (contentLocation != null) {
				if (contentLocation.getId() != null){
					list.add(contentLocation);
				}
			}
			
		});
		return list;
	}

	public ContentLocation findAndRemoveById(String id,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		@SuppressWarnings("unchecked")
		ContentLocation contentLocation = (ContentLocation) mongoTemplate.findAndRemove(query, ContentLocation.class);
		logger.debug("Deleted Content Location : " + contentLocation);
		return contentLocation;
	}

	public ContentLocation findAndRemoveByIdAndVersion(String id, String version,String tenantId) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("_id").is(id).andOperator(Criteria.where("version").is(Long.valueOf(version))));
		@SuppressWarnings("unchecked")
		ContentLocation contentLocation = (ContentLocation) mongoTemplate.findAndRemove(query, ContentLocation.class);
		logger.debug("Deleted Content Location: " + contentLocation);
		return contentLocation;
	}

	@Override
	public ContentLocation findAndModify(String id, Map<String, String> updateParams, Long version,String tenantId) {
		Query query = new Query();
		if (version == null) {
			query.addCriteria(Criteria.where("_id").is(id));
		} else {
			query.addCriteria(Criteria.where("_id").is(id).andOperator(Criteria.where("version").is(version)));
		}

		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		for (Map.Entry<String, String> entry : updateParams.entrySet()) {
			update.set(entry.getKey(), entry.getValue());
		}

		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		@SuppressWarnings("unchecked")
		ContentLocation contentLocation = (ContentLocation) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), ContentLocation.class);
		logger.debug("Updated ContentLocation - " + contentLocation);
		return contentLocation;
	}

	@Override
	public ContentLocation findAndIncrementSharedCount(String id,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());
		update.inc("sharedCount", 1);
		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		@SuppressWarnings("unchecked")
		ContentLocation contentLocation = (ContentLocation) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), ContentLocation.class);
		logger.debug("Updated ContentLocation - " + contentLocation);
		return contentLocation;
	}

	@Override
	public ContentLocation findAndDecrementSharedCount(String id,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());
		update.inc("sharedCount", -1);
		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		@SuppressWarnings("unchecked")
		ContentLocation contentLocation = (ContentLocation) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), ContentLocation.class);
		logger.debug("Updated ContentLocation - " + contentLocation);
		return contentLocation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContentLocation> findAllDeletedContentLocations() {
		Query query = new Query();
		query.addCriteria(Criteria.where("deleted").is("true"));

		return mongoTemplate.find(query, ContentLocation.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContentLocation> findAllDanglingContentLocations() {
		Query query = new Query();
		Date compareDate = DateUtils.addDays(new Date(), -2);
		query.addCriteria(Criteria.where("accessDateTime").lte(compareDate));
		query.addCriteria(Criteria.where("deleted").ne("true"));
		return mongoTemplate.find(query, ContentLocation.class);
	}

	@Override
	public ContentLocation findAndDeleteByLocationId(String locationId,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("locationId").is(locationId));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		@SuppressWarnings("unchecked")
		ContentLocation contentLocation = (ContentLocation) mongoTemplate.findAndRemove(query, ContentLocation.class);
		logger.debug("Deleted Content Location: " + contentLocation);
		return contentLocation;
	}

}
