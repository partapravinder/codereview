package com.newgen.doa.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openjdk.jol.info.GraphLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.newgen.dao.AnnotationDao;
import com.newgen.model.Annotation;
import com.newgen.model.InOutParameters;
import com.newgen.repository.AnnotationRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class AnnotationDaoImpl implements AnnotationDao {

	// private static final Logger logger =
	// LoggerFactory.getLogger(AnnotationDaoImpl.class);

	@Autowired
	WrapperMongoService<Annotation> mongoTemplate;

	@Autowired
	AnnotationRepository annotationRepository;

	public static final String VERSION_PARAM = "version";

	public static final String DELETED_PARAM = "deleted";

	public static final String REVISEDDATETIME_PARAM = "revisedDateTime";
	public static final String ACCESSDATETIME_PARAM = "accessDateTime";

	@Override
	public InOutParameters insert(Annotation annotation) {
		InOutParameters inOutParameters = new InOutParameters();
		Double resSize = 0.0;
		System.out.println("insert content=>" + annotation.toString());
		Annotation annotation2 = annotationRepository.insert(annotation);
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(annotation).totalSize()) / 1024.0);
		if (annotation2 != null) {
			resSize = (GraphLayout.parseInstance(annotation2).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setAnnotation(annotation2);
		return inOutParameters;
	}

	@Override
	public InOutParameters findAllAnnotations(Map<String, String[]> paramMap, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
		Double resSize = 0.0;
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			try {
				// logger.debug(entry.getKey()+"=>"+URLDecoder.decode(entry.getValue()[0],"UTF-8"));
				query.addCriteria(Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0], "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		List<Annotation> annotations = mongoTemplate.find(query, Annotation.class);
		;
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		// query.addCriteria(Criteria.where(DELETED_PARAM).ne("true"));
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(annotations).totalSize()) / 1024.0);
		if (annotations != null) {
			resSize = (GraphLayout.parseInstance(annotations).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setAnnotations(annotations);
		return inOutParameters;
	}

	@Override
	public InOutParameters findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
		Double resSize = 0.0;
		query.addCriteria(Criteria.where("id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Annotation annotation = (Annotation) mongoTemplate.findAndRemove(query, Annotation.class);
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		if (annotation != null) {
			resSize = (GraphLayout.parseInstance(annotation).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setAnnotation(annotation);
		return inOutParameters;
	}

	@Override
	public InOutParameters findAndModify(String id, Map<String, String> updateFolderParams, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
		Double resSize = 0.0;
		query.addCriteria(Criteria.where("id").is(id));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		for (Map.Entry<String, String> entry : updateFolderParams.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) || "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())
					|| "ownerName".equalsIgnoreCase(entry.getKey()) || "ownerId".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			update.set(entry.getKey(), entry.getValue());
		}
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Annotation updatedAnnotation = (Annotation) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), Annotation.class);

		inOutParameters.setRequestPayloadSize(((GraphLayout.parseInstance(query).totalSize()) / 1024.0)
				+ (GraphLayout.parseInstance(update).totalSize()) / 1024.0);
		if (updatedAnnotation != null) {
			resSize = (GraphLayout.parseInstance(updatedAnnotation).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setAnnotation(updatedAnnotation);
		return inOutParameters;
	}

}
