package com.newgen.doa.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.newgen.dao.AsyncFolderDao;
import com.newgen.model.AsyncFolderOperation;
import com.newgen.model.InOutParameters;
import com.newgen.model.AsyncFolderOperation.Status;
import com.newgen.repository.AsyncFolderRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class AsyncFolderDaoImpl implements AsyncFolderDao {

	private static final Logger logger = LoggerFactory.getLogger(AsyncFolderDaoImpl.class);

	@SuppressWarnings("rawtypes")
	@Autowired
	WrapperMongoService mongoTemplate;

	@Autowired
	AsyncFolderRepository asyncFolderRepository;

	@Override
	public InOutParameters insert(AsyncFolderOperation asyncFolderOperation) {
		AsyncFolderOperation asyncFolderOperation2 = null;
		InOutParameters inOutParams = new InOutParameters();
		inOutParams.setRequestPayloadSize((GraphLayout.parseInstance(asyncFolderOperation).totalSize()) / 1024.0);
		try {
			asyncFolderOperation2 = asyncFolderRepository.insert(asyncFolderOperation);
			inOutParams.setResponsePayloadSize((GraphLayout.parseInstance(asyncFolderOperation2).totalSize()) / 1024.0);
			inOutParams.setAsyncFolderOperation(asyncFolderOperation2);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (asyncFolderOperation2 == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				asyncFolderOperation2 = asyncFolderRepository.insert(asyncFolderOperation);
				inOutParams.setResponsePayloadSize(
						(GraphLayout.parseInstance(asyncFolderOperation2).totalSize()) / 1024.0);
				inOutParams.setAsyncFolderOperation(asyncFolderOperation2);
			}
			logger.debug(" " + asyncFolderOperation2);
		}
		return inOutParams;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InOutParameters findById(String id, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParams = new InOutParameters();
		query.addCriteria(Criteria.where("id").is(id));
		inOutParams.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		AsyncFolderOperation asyncFolderOperation = null;
		try {
			asyncFolderOperation = (AsyncFolderOperation) mongoTemplate.findOne(query, AsyncFolderOperation.class);
			inOutParams.setResponsePayloadSize((GraphLayout.parseInstance(asyncFolderOperation).totalSize()) / 1024.0);
			inOutParams.setAsyncFolderOperation(asyncFolderOperation);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (asyncFolderOperation == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				asyncFolderOperation = (AsyncFolderOperation) mongoTemplate.findOne(query, AsyncFolderOperation.class);
				inOutParams
						.setResponsePayloadSize((GraphLayout.parseInstance(asyncFolderOperation).totalSize()) / 1024.0);
				inOutParams.setAsyncFolderOperation(asyncFolderOperation);
			}
			logger.debug(" " + asyncFolderOperation);
		}
//		AsyncFolderOperation asyncFolderOperation = null;
//		Optional<AsyncFolderOperation> dbAsyncFolderOperation = asyncFolderRepository.findById(id);
//		if(dbAsyncFolderOperation.isPresent()) {
//			//operate on existing contentLocation
//			asyncFolderOperation = dbAsyncFolderOperation.get();
//		} else {
//		    //there is no Customer in the repo with 'id'
//		}
		return inOutParams;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InOutParameters updateStatus(String id, Status status, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("id").is(id));

		Update update = new Update();
		update.set("status", status);
		update.set("statusChangeDateTime", new Date());

		InOutParameters inOutParams = new InOutParameters();
		inOutParams.setRequestPayloadSize(((GraphLayout.parseInstance(query).totalSize()) / 1024.0)
				+ ((GraphLayout.parseInstance(update).totalSize()) / 1024.0));

		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		AsyncFolderOperation asyncFolderOperation = null;
		try {
			asyncFolderOperation = (AsyncFolderOperation) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), AsyncFolderOperation.class);
			inOutParams.setResponsePayloadSize((GraphLayout.parseInstance(asyncFolderOperation).totalSize()) / 1024.0);
			inOutParams.setAsyncFolderOperation(asyncFolderOperation);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (asyncFolderOperation == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				asyncFolderOperation = (AsyncFolderOperation) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), AsyncFolderOperation.class);
				inOutParams
						.setResponsePayloadSize((GraphLayout.parseInstance(asyncFolderOperation).totalSize()) / 1024.0);
				inOutParams.setAsyncFolderOperation(asyncFolderOperation);
			}
			logger.debug(" " + asyncFolderOperation);
		}
		logger.debug("Updated AsyncFolderOperation - " + asyncFolderOperation);
		return inOutParams;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InOutParameters findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		InOutParameters inOutParams = new InOutParameters();
		inOutParams.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);

		AsyncFolderOperation asyncFolderOperation = null;
		try {
			asyncFolderOperation = (AsyncFolderOperation) mongoTemplate.findAndRemove(query,
					AsyncFolderOperation.class);
			inOutParams.setResponsePayloadSize((GraphLayout.parseInstance(asyncFolderOperation).totalSize()) / 1024.0);
			inOutParams.setAsyncFolderOperation(asyncFolderOperation);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (asyncFolderOperation == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				asyncFolderOperation = (AsyncFolderOperation) mongoTemplate.findAndRemove(query,
						AsyncFolderOperation.class);
				inOutParams
						.setResponsePayloadSize((GraphLayout.parseInstance(asyncFolderOperation).totalSize()) / 1024.0);
				inOutParams.setAsyncFolderOperation(asyncFolderOperation);
			}
			logger.debug(" " + asyncFolderOperation);
		}
		logger.debug("Deleted AsyncFolderOperation : " + asyncFolderOperation);
		return inOutParams;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AsyncFolderOperation> findAllCompletedAndFailed() {
		Query query = new Query();

		Date compareDate = DateUtils.addMinutes(new Date(), -5);
		Criteria ack = Criteria.where("status").is(Status.COMPLETED);
		Criteria fail = Criteria.where("status").is(Status.FAILED);
		Criteria ackorfail = new Criteria().orOperator(ack, fail);
		Criteria threshold_time = Criteria.where("statusChangeDateTime").lte(compareDate);
		query.addCriteria(new Criteria().andOperator(threshold_time, ackorfail));
//		query.addCriteria(statusChangeDateTime
//				(Criteria.where("status").is(Status.COMPLETED).orOperator(Criteria.where("status").is(Status.FAILED)))
//						.andOperator(Criteria.where("statusChangeDateTime").lt(compareDate)));

		List<AsyncFolderOperation> asyncFolderOperations = null;
		try {
			asyncFolderOperations = mongoTemplate.find(query, AsyncFolderOperation.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (asyncFolderOperations == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				asyncFolderOperations = mongoTemplate.find(query, AsyncFolderOperation.class);
			}
			logger.debug(" " + asyncFolderOperations);
		}
		return asyncFolderOperations;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AsyncFolderOperation updateProgress(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Update update = new Update();
		update.inc("progress", 1);
		update.set("statusChangeDateTime", new Date());

		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		AsyncFolderOperation asyncFolderOperation = null;
		try {
			asyncFolderOperation = (AsyncFolderOperation) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), AsyncFolderOperation.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (asyncFolderOperation == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				asyncFolderOperation = (AsyncFolderOperation) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), AsyncFolderOperation.class);
			}
			logger.debug(" " + asyncFolderOperation);
		}
		logger.debug("Updated AsyncFolderOperation - " + asyncFolderOperation);
		return asyncFolderOperation;
	}
}
