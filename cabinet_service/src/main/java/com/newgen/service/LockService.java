package com.newgen.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.LockDao;
import com.newgen.exception.CustomException;
import com.newgen.model.Lock;

@Service
public class LockService extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(LockService.class);

	@Autowired
	LockDao lockDao;

	public Lock getLock(String id, String guid, String lockType,String tenantId) throws CustomException {
		logger.debug("Getting lock for id: " + id);
		Lock lock = null;
		try {
			lock = findById(id);
			if (lock != null) {
				if ("exclusive".equalsIgnoreCase(lock.getLockType())) {
					throwLockExistsException();
				} else if ("shared".equalsIgnoreCase(lock.getLockType())) {
					if ("exclusive".equalsIgnoreCase(lockType)) {
						throwLockExistsException();
					} else if ("shared".equalsIgnoreCase(lockType)) {
						lock = incrementSharedCount(id,tenantId);
					}
				}
			} else {
				lock = insert(id, guid, lockType,tenantId);
			}
		} catch (DuplicateKeyException ex) {
			logger.warn("Duplicate key exception while getting lock",ex);
			return getLock(id, guid, lockType,tenantId);
		}
		return lock;
	}

	public Lock releaseLock(String id,String tenantId) {
		logger.debug("Releasing lock for id: " + id);
		Lock lock = findById(id);
		if (lock != null) {
			if ("exclusive".equalsIgnoreCase(lock.getLockType())) {
				delete(id,tenantId);
			} else if ("shared".equalsIgnoreCase(lock.getLockType())) {
				int sharedCount = lock.getSharedCount();
				if (sharedCount == 1) {
					delete(id,tenantId);
				} else {
					lock = decrementSharedCount(id,tenantId);
				}
			}
		}
		return lock;
	}

	public Lock insert(String id, String guid, String type,String tenantId) throws CustomException {
		Lock lock = new Lock(id, guid, new Date(), type, 1,tenantId);
		logger.debug("Creating lock for :" + lock.toString());
		return lockDao.insert(lock);
	}

	public void delete(String id,String tenantId) {
		logger.debug("Deleting lock for id: " + id);
		lockDao.delete(id,tenantId);
	}

	public Lock incrementSharedCount(String id,String tenantId) {
		logger.debug("Increment shared count for lock with id: " + id);
		return lockDao.findAndIncrementSharedCount(id,tenantId);
	}

	public Lock decrementSharedCount(String id,String tenantId) {
		logger.debug("Decrement shared count for lock with id: " + id);
		return lockDao.findAndDecrementSharedCount(id,tenantId);
	}

	public Lock findById(String id) {
		logger.debug("Finding lock with id: " + id);
		return lockDao.findById(id);
	}
}
