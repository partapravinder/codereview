package com.newgen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.Annotation;

@Repository
public interface AnnotationRepository extends MongoRepository<Annotation, String> {
	
}
