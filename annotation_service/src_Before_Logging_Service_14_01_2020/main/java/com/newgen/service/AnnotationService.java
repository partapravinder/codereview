package com.newgen.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.AnnotationDao;
import com.newgen.model.Annotation;

@Service
public class AnnotationService extends ExceptionThrower  {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationService.class);

	@Autowired
	AnnotationDao annotationDao;

	public Annotation insert(Annotation annotation) {
		logger.debug("Insering a new Annotation");
		return annotationDao.insert(annotation);	
	}
	
	/*public Annotation find(String id) throws CustomException {
		return annotationDao.findAndModify(id);	
	}*/
	
	public Annotation update(String id, Map<String, String> updateFolderParams,String tenantId) {
		logger.debug("Updating Annotation with id: " + id);
		return annotationDao.findAndModify(id, updateFolderParams,tenantId );	
	}
	
	public void delete(String id,String tenantId) {
		logger.debug("Deleting Annotation with id: " + id);
		annotationDao.findAndRemoveById(id,tenantId);	
	}

	/*public List<Annotation> search(Map<String, String[]> allRequestParams) {
		logger.debug("Searching for Annotation with : " + allRequestParams);
		return annotationDao.findAllAnnotations(allRequestParams);
	}*/

	public List<Annotation> search(Map<String, String[]> allRequestParams,String tenantId) {
		logger.debug("Searching for Annotations  " + allRequestParams);
		return annotationDao.findAllAnnotations(allRequestParams,tenantId);
	}

}
