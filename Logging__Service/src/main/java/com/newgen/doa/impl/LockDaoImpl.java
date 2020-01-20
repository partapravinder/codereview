/*
 * package com.newgen.doa.impl;
 * 
 * import java.util.Optional;
 * 
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.data.mongodb.core.FindAndModifyOptions; import
 * org.springframework.data.mongodb.core.query.Criteria; import
 * org.springframework.data.mongodb.core.query.Query; import
 * org.springframework.data.mongodb.core.query.Update; import
 * org.springframework.stereotype.Repository;
 * 
 * import com.newgen.dao.LockDao; import com.newgen.model.Lock; import
 * com.newgen.repository.LockRepository; import
 * com.newgen.wrapper.service.WrapperMongoService;
 * 
 * @Repository public class LockDaoImpl implements LockDao {
 * 
 * private static final Logger logger =
 * LoggerFactory.getLogger(LockDaoImpl.class);
 * 
 * @SuppressWarnings("rawtypes")
 * 
 * @Autowired WrapperMongoService mongoTemplate;
 * 
 * @Autowired LockRepository lockRepository;
 * 
 * @Override public Lock insert(Lock lock) { return lockRepository.insert(lock);
 * }
 * 
 * @Override public Lock delete(String id,String tenantId) { Query query = new
 * Query(); query.addCriteria(Criteria.where("id").is(id));
 * query.addCriteria(Criteria.where("tenantId").is(tenantId));
 * logger.debug("Deleting folder : " + id);
 * 
 * @SuppressWarnings("unchecked") Lock lock = (Lock)
 * mongoTemplate.findAndRemove(query, Lock.class);
 * logger.debug("Deleted folder : " + lock); return lock; }
 * 
 * @Override public Lock findById(String id) { Lock lock = null; Optional<Lock>
 * dbLock = lockRepository.findById(id); if(dbLock.isPresent()) { //operate on
 * existing lock lock = dbLock.get(); } else { //there is no Lock in the repo
 * with 'id' } return lock; }
 * 
 * @Override public Lock save(Lock lock) { return lockRepository.save(lock); }
 * 
 * @Override public Lock findAndIncrementSharedCount(String id,String tenantId)
 * { Query query = new Query(); query.addCriteria(Criteria.where("_id").is(id));
 * query.addCriteria(Criteria.where("tenantId").is(tenantId)); Update update =
 * new Update();
 * 
 * update.inc("sharedCount", 1); // FindAndModifyOptions().returnNew(true) =
 * newly updated document // FindAndModifyOptions().returnNew(false) = old
 * document (not updated // yet)
 * 
 * @SuppressWarnings("unchecked") Lock lock = (Lock)
 * mongoTemplate.findAndModify(query, update, new
 * FindAndModifyOptions().returnNew(true), Lock.class);
 * logger.debug("Updated lock - " + lock); return lock; }
 * 
 * @Override public Lock findAndDecrementSharedCount(String id,String tenantId)
 * { Query query = new Query(); query.addCriteria(Criteria.where("_id").is(id));
 * query.addCriteria(Criteria.where("tenantId").is(tenantId)); Update update =
 * new Update();
 * 
 * update.inc("sharedCount", -1); // FindAndModifyOptions().returnNew(true) =
 * newly updated document // FindAndModifyOptions().returnNew(false) = old
 * document (not updated // yet)
 * 
 * @SuppressWarnings("unchecked") Lock lock = (Lock)
 * mongoTemplate.findAndModify(query, update, new
 * FindAndModifyOptions().returnNew(true), Lock.class);
 * logger.debug("Updated lock - " + lock); return lock; }
 * 
 * }
 */