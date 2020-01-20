package com.newgen.dao;

import java.util.Map;

import com.newgen.model.Annotation;
import com.newgen.model.InOutParameters;

public interface AnnotationDao {

	public InOutParameters insert(Annotation annotation);

	public InOutParameters findAndRemoveById(String id, String tenantId);

	public InOutParameters findAndModify(String id, Map<String, String> updateFolderParams, String tenantId);

	public InOutParameters findAllAnnotations(Map<String, String[]> paramMap, String tenantId);

}
