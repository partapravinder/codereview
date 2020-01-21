package com.newgen.wrapper.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.newgen.wrapper.service.WrapperMongoService;

@Component
@Profile({ "production", "default" })
public class WrapperMongoServiceImpl<T> implements WrapperMongoService<T> {

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass) {
		return mongoTemplate.findAndModify(query, update, options, entityClass);
	}

	@Override
	public T findAndRemove(Query query, Class<T> entityClass) {
		return mongoTemplate.findAndRemove(query, entityClass);
	}

	@Override
	public List<T> find(Query query, Class<T> entityClass) {
		return mongoTemplate.find(query, entityClass);
	}

	@Override
	public T findOne(Query query, Class<T> entityClass) {
		return mongoTemplate.findOne(query, entityClass);
	}

	@Override
	public void insert(BasicDBObject dbObj, String collectionName) {
		mongoTemplate.insert(dbObj, collectionName);
	}

	@Override
	public T findAndModify(Query query, Update update, FindAndModifyOptions returnNew, Class<T> entityClass,
			String collectionName) {
		return mongoTemplate.findAndModify(query, update, returnNew, entityClass,
				collectionName);
	}

	@Override
	public T findAndRemove(Query query, Class<T> entityClass, String collectionName) {
		return mongoTemplate.findAndRemove(query, entityClass, collectionName);
	}

	@Override
	public List<T> find(Query query, Class<T> entityClass, String collectionName) {
		return mongoTemplate.find(query, entityClass, collectionName);
	}

}
