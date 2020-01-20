package com.newgen.doa.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.newgen.dao.AnnotationDao;
import com.newgen.model.Annotation;
import com.newgen.repository.AnnotationRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class AnnotationDaoImpl implements AnnotationDao{

	//private static final Logger logger = LoggerFactory.getLogger(AnnotationDaoImpl.class);

	@Autowired
	WrapperMongoService<Annotation> mongoTemplate;

	@Autowired
	AnnotationRepository annotationRepository;

	public static final String VERSION_PARAM = "version";

	public static final String DELETED_PARAM = "deleted";

	public static final String REVISEDDATETIME_PARAM = "revisedDateTime";
	public static final String ACCESSDATETIME_PARAM = "accessDateTime";

	@Override
	public Annotation insert(Annotation annotation) {
		System.out.println("insert content=>"+annotation.toString());
		return annotationRepository.insert(annotation);
	}
	
	@Override
	public List<Annotation> findAllAnnotations(Map<String, String[]> paramMap,String tenantId) {
		Query query = new Query();
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			try {
				//logger.debug(entry.getKey()+"=>"+URLDecoder.decode(entry.getValue()[0],"UTF-8"));
				query.addCriteria(Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0],"UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		//query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		return mongoTemplate.find(query, Annotation.class);
	}
	
	@Override
	public Annotation findAndRemoveById(String id,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Annotation annotation = (Annotation) mongoTemplate.findAndRemove(query, Annotation.class);
		return annotation;
	}

	@Override
	public Annotation findAndModify(String id, Map<String, String> updateFolderParams,String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
				
		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());
		
		for (Map.Entry<String, String> entry : updateFolderParams.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) 
					|| "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())
					|| "ownerName".equalsIgnoreCase(entry.getKey())
					|| "ownerId".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			update.set(entry.getKey(), entry.getValue());
		}	
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Annotation updatedAnnotation = (Annotation) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), Annotation.class);
		
		return updatedAnnotation;
	}
	
}
