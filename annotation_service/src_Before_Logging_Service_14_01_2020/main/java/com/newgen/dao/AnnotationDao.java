package com.newgen.dao;

import java.util.List;
import java.util.Map;

import com.newgen.model.Annotation;

public interface AnnotationDao {
	
	public Annotation insert(Annotation annotation);
	
	public Annotation findAndRemoveById(String id,String tenantId);
	
	public Annotation findAndModify(String id, Map<String, String> updateFolderParams,String tenantId);

	public List<Annotation> findAllAnnotations(Map<String, String[]> paramMap,String tenantId);

}
