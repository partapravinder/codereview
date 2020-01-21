package com.newgen.doa.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.newgen.dao.LockDao;
import com.newgen.model.Content;
import com.newgen.model.Lock;
import com.newgen.repository.LockRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class LockDaoImpl implements LockDao {
	private static final Logger logger = LoggerFactory.getLogger(LockDaoImpl.class);
	@SuppressWarnings("rawtypes")
	@Autowired
	WrapperMongoService mongoTemplate;

	@Autowired
	LockRepository lockRepository;

	@Override
	public Lock insert(Lock lock) {
		//return lockRepository.insert(lock);
		Lock lockRetrun = null;
		try {
			lockRetrun = lockRepository.insert(lock);

		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {

			if (lockRetrun == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				lockRetrun = lockRepository.insert(lock);
			}
			logger.debug("inserted Lock : " + lockRetrun);
		}

		return lockRetrun;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Lock delete(String id, String tenantId) {

		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		logger.debug("Deleting Lock : " + id);
		//Lock lock = (Lock) mongoTemplate.findAndRemove(query, Lock.class);
		
		
		Lock lock = null;
		try {
			lock = (Lock) mongoTemplate.findAndRemove(query, Lock.class);

		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {

			if (lock == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				lock = (Lock) mongoTemplate.findAndRemove(query, Lock.class);
			}
			logger.debug("Deleted Lock : " + lock);
		}

		return lock;
	}

	@Override
	public Lock findById(String id) {
		Lock lock = null;
		/*
		 * //Optional<Lock> dbLock = lockRepository.findById(id); if
		 * (dbLock.isPresent()) { // operate on existing lock lock = dbLock.get(); }
		 * else { // there is no Lock in the repo with 'id' }
		 */
		Optional<Lock> dbLock = null;
		try {
			dbLock = lockRepository.findById(id);
			if (dbLock.isPresent()) {
				// operate on existing lock
				lock = dbLock.get();
			} else {
				// there is no Lock in the repo with 'id'
			}

		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {

			if (dbLock == null) {
				dbLock = lockRepository.findById(id);
				if (dbLock.isPresent()) {
					// operate on existing lock
					lock = dbLock.get();
				} else {
					// there is no Lock in the repo with 'id'
				}
			}
			logger.debug("Updated lock - " + lock);
		}

		return lock;
	}

	@Override
	public Lock save(Lock lock) {

		// return lockRepository.save(lock);

		Lock lockReturn = null;
		try {
			lockReturn = lockRepository.save(lock);

		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {

			if (lockReturn == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				lockReturn = lockRepository.save(lock);
			}
			logger.debug("Saved lock : " + lockReturn);
		}
		return lockReturn;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Lock findAndIncrementSharedCount(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Update update = new Update();

		update.inc("sharedCount", 1);
		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		// Lock lock = (Lock) mongoTemplate.findAndModify(query, update, new
		// FindAndModifyOptions().returnNew(true),
		// Lock.class);

		Lock lock = null;
		try {
			lock = (Lock) mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true),
					Lock.class);

		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {

			if (lock == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				lock = (Lock) mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true),
						Lock.class);
			}
			logger.debug("Updated lock - " + lock);
		}

		return lock;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Lock findAndDecrementSharedCount(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		Update update = new Update();

		update.inc("sharedCount", -1);
		// FindAndModifyOptions().returnNew(true) = newly updated document
		// FindAndModifyOptions().returnNew(false) = old document (not updated
		// yet)
		// Lock lock = (Lock) mongoTemplate.findAndModify(query, update, new
		// FindAndModifyOptions().returnNew(true),
		// Lock.class);

		Lock lock = null;
		try {
			lock = (Lock) mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true),
					Lock.class);

		} catch (UncategorizedMongoDbException ex) {
			logger.error(ex.getMessage());
		} finally {

			if (lock == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				lock = (Lock) mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true),
						Lock.class);
			}
			logger.debug("Updated lock - " + lock);
		}

		return lock;
	}

}
