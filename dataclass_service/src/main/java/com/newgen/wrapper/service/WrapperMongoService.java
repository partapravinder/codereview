package com.newgen.wrapper.service;

import java.util.List;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;

public interface WrapperMongoService<T> {
	T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass);

	T findAndRemove(Query query, Class<T> entityClass);

	List<T> find(Query query, Class<T> entityClass);

	T findOne(Query query, Class<T> entityClass);

	void insert(BasicDBObject dbObj, String collectionName);

	T findAndModify(Query query, Update update, FindAndModifyOptions returnNew, Class<T> entityClass,
			String collectionName);

	T findAndRemove(Query query, Class<T> entityClass, String collectionName);

	List<T> find(Query query, Class<T> entityClass, String collectionName);
	
	<T> AggregationResults<T> aggregate(Aggregation query,String collectionName, Class<T> entityClass );
}
