package com.newgen.wrapper.service;

import java.util.List;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;

public interface WrapperMongoService<T> {

	T findOne(Query query, Class<T> entityClass);

	void insert(BasicDBObject dbObj, String collectionName);
	
	T findAndRemove(Query query, Class<T> entityClass);

	void updateFirst(Query query, Update update, Class<T> entityClass);

	T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass);
	
	List<T> find(Query query, Class<T> entityClass);

}
