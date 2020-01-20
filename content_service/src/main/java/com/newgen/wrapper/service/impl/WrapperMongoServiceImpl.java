package com.newgen.wrapper.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.newgen.doa.impl.ContentDaoImpl;
import com.newgen.wrapper.service.WrapperMongoService;

@Component
@Profile({ "production", "default" })
public class WrapperMongoServiceImpl<T> implements WrapperMongoService<T> {

	private static final Logger logger = LoggerFactory.getLogger(WrapperMongoServiceImpl.class);
	
	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass) {
		try {
			return mongoTemplate.findAndModify(query, update, options, entityClass);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return mongoTemplate.findAndModify(query, update, options, entityClass);
        }
	}

	@Override
	public T findAndRemove(Query query, Class<T> entityClass) {
		try {
			return mongoTemplate.findAndRemove(query, entityClass);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return mongoTemplate.findAndRemove(query, entityClass);
        }
	}

	@Override
	public List<T> find(Query query, Class<T> entityClass) {
		try {
			return mongoTemplate.find(query, entityClass);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return mongoTemplate.find(query, entityClass);
        }
	}

	@Override
	public T findOne(Query query, Class<T> entityClass) {
		try {
			return mongoTemplate.findOne(query, entityClass);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return mongoTemplate.findOne(query, entityClass);
        }
	}

	@Override
	public void insert(BasicDBObject dbObj, String collectionName) {
		try {
			mongoTemplate.insert(dbObj, collectionName);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	mongoTemplate.insert(dbObj, collectionName);
        }
	}

	@Override
	public T findAndModify(Query query, Update update, FindAndModifyOptions returnNew, Class<T> entityClass,
			String collectionName) {
		try {
			return mongoTemplate.findAndModify(query, update, returnNew, entityClass,
					collectionName);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return mongoTemplate.findAndModify(query, update, returnNew, entityClass,
    				collectionName);
        }
	}

	@Override
	public T findAndRemove(Query query, Class<T> entityClass, String collectionName) {
		try {
			return mongoTemplate.findAndRemove(query, entityClass, collectionName);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return mongoTemplate.findAndRemove(query, entityClass, collectionName);
        }
	}

	@Override
	public List<T> find(Query query, Class<T> entityClass, String collectionName) {
		try {
			return mongoTemplate.find(query, entityClass, collectionName);
        } catch (Exception ex) {
        	logger.error(ex.getMessage());
        	logger.debug("Exception thrown---------retrying action.... ");
        	return mongoTemplate.find(query, entityClass, collectionName);
        }
	}

}
