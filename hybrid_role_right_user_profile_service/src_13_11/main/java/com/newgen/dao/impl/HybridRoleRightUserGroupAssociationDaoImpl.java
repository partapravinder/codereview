package com.newgen.dao.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Sets;
import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.HybridRoleRightGroupUserAssociationDao;
import com.newgen.dao.HybridRoleRightUserGroupAssociationDao;
import com.newgen.exception.CustomException;
import com.newgen.model.GroupUserAssociation;
import com.newgen.model.UserGroupAssociation;
import com.newgen.repository.UserGroupAssociationRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class HybridRoleRightUserGroupAssociationDaoImpl implements HybridRoleRightUserGroupAssociationDao {
	private static final Logger logger = LoggerFactory.getLogger(HybridRoleRightUserGroupAssociationDaoImpl.class);

	@Autowired
	UserGroupAssociationRepository userGroupAssociationRepository;

	@Autowired
	HybridRoleRightGroupUserAssociationDao hybridRoleRightGroupUserAssociationDao;

	@Autowired
	WrapperMongoService<UserGroupAssociation> mongoTemplate;

	@Autowired
	ExceptionThrower exceptionThrower;

	@Override
	public UserGroupAssociation insert(UserGroupAssociation userGroupAssociation) {
		userGroupAssociation.setCreationDateTime(new Date());
		userGroupAssociation.setAccessDateTime(new Date());
		return userGroupAssociationRepository.insert(userGroupAssociation);
	}

	@Override
	public List<UserGroupAssociation> insertAll(List<UserGroupAssociation> userGroupAssociation) {
		userGroupAssociation.stream().forEach(u -> {
			u.setCreationDateTime(new Date());
			u.setAccessDateTime(new Date());
		});
		return userGroupAssociationRepository.insert(userGroupAssociation);
	}

	@Override
	public List<UserGroupAssociation> saveAll(List<UserGroupAssociation> userGroupAssociation) {
		userGroupAssociation.stream().forEach(u -> {
			u.setCreationDateTime(new Date());
			u.setAccessDateTime(new Date());
		});
		return userGroupAssociationRepository.saveAll(userGroupAssociation);
	}

	@Override
	public UserGroupAssociation save(UserGroupAssociation userGroupAssociation) {
		return userGroupAssociationRepository.save(userGroupAssociation);
	}

	@Override
	public UserGroupAssociation findById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		UserGroupAssociation userGroupAssociation = (UserGroupAssociation) mongoTemplate.findOne(query,
				UserGroupAssociation.class);
		logger.debug("UserGroupAssociation found : " + userGroupAssociation);
		return userGroupAssociation;
	}

	@Override
	public UserGroupAssociation findByGroup(String group, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("groupId").is(group));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		UserGroupAssociation userGroupAssociation = (UserGroupAssociation) mongoTemplate.findOne(query,
				UserGroupAssociation.class);
		return userGroupAssociation;
	}

	// @SuppressWarnings("unchecked")
	@Override
	public UserGroupAssociation findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		UserGroupAssociation userGroupAssociation = (UserGroupAssociation) mongoTemplate.findAndRemove(query,
				UserGroupAssociation.class);

		// remove groupId from users
		// find users that are part of this group id

		userGroupAssociation.getUserIds().forEach(user -> {
			GroupUserAssociation groupUserAssociation = hybridRoleRightGroupUserAssociationDao.findById(user, tenantId);
			groupUserAssociation.getGroupIds().remove(id);
			hybridRoleRightGroupUserAssociationDao.findAndModifyByGroup(user, groupUserAssociation, tenantId);
		});

		logger.debug("Deleted userGroupAssociation : " + userGroupAssociation);
		return userGroupAssociation;
	}

	

	@Override
	@SuppressWarnings("unchecked")
	public UserGroupAssociation findAndModify(String id, Map<String, Object> updateParams, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(id));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		for (Map.Entry<String, Object> entry : updateParams.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
					|| "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			// update.set(entry.getKey(), entry.getValue());
			if (entry.getValue() != null && !(entry.getValue() instanceof String)) {
				try {
					if ((List<Object>) entry.getValue() instanceof List) {
						List<Object> list = (List<Object>) entry.getValue();
						if (list.size() > 0) {
							update.pushAll(entry.getKey(), Sets.newHashSet(list).toArray());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				update.set(entry.getKey(), entry.getValue());
			}
		}

		UserGroupAssociation userGroupAssociation = (UserGroupAssociation) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), UserGroupAssociation.class);

		logger.debug("userGroupAssociation : " + userGroupAssociation);
		return userGroupAssociation;
	}

	@Override
	@SuppressWarnings("unchecked")
	public UserGroupAssociation findAndModifyByGroup(String groupId, Map<String, Object> updateParams,
			String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(groupId));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		Set<Object> updateduserids = new HashSet<Object>();
		Set<Object> existinguserids = new HashSet<Object>();

		System.out.println(groupId);
		System.out.println(tenantId);
		UserGroupAssociation userGroupAssociation1 = findById(groupId, tenantId);
		if(userGroupAssociation1==null) {
			System.out.println("nulllllllllllllllllllllllllllllllllllll");
			//TODO throw exception that record not found.
		}
		if(userGroupAssociation1.getUserIds()!=null) {
			existinguserids.addAll(userGroupAssociation1.getUserIds());			
		}

		if (updateParams != null) {
			for (Map.Entry<String, Object> entry : updateParams.entrySet()) {
				if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
						|| "creationDateTime".equalsIgnoreCase(entry.getKey())
						|| "accessDateTime".equalsIgnoreCase(entry.getKey())
						|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
					continue;
				}

				if (entry.getValue() != null && !(entry.getValue() instanceof String)) {
					try {
						if ((List<Object>) entry.getValue() instanceof List) {
							List<Object> list = (List<Object>) entry.getValue();
							if (list.size() > 0) {
								updateduserids = Sets.newHashSet(list);
								existinguserids.addAll(updateduserids);
								update.set(entry.getKey(), existinguserids.toArray());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					update.set(entry.getKey(), entry.getValue());
				}
			}
		}
		UserGroupAssociation userGroupAssociation = (UserGroupAssociation) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), UserGroupAssociation.class);

		if (updateduserids.size() > 0) {
			for (Object user : updateduserids) {
				GroupUserAssociation groupUserAssociation = hybridRoleRightGroupUserAssociationDao
						.findById(user.toString(), tenantId);
				if (groupUserAssociation != null) {
					if (!updateduserids.contains(user.toString())) {
						groupUserAssociation.getGroupIds().remove(groupId);
					} else {
						groupUserAssociation.getGroupIds().add(groupId);
					}
					hybridRoleRightGroupUserAssociationDao.findAndModifyByGroup(user.toString(), groupUserAssociation,
							tenantId);
				} else {
					GroupUserAssociation groupUserAssociation1 = new GroupUserAssociation(tenantId, user.toString(),
							Arrays.asList(groupId));
					hybridRoleRightGroupUserAssociationDao.insert(groupUserAssociation1);
				}
			}
		}

		logger.debug("Updated User Profile : " + userGroupAssociation);
		return userGroupAssociation;
	}
	//

	@Override
	public List<UserGroupAssociation> findAll(Map<String, String[]> paramMap, String tenantId) {

		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		if (paramMap != null) {
			for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
				if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
						|| "creationDateTime".equalsIgnoreCase(entry.getKey())
						|| "accessDateTime".equalsIgnoreCase(entry.getKey())
						|| "tenantId".equalsIgnoreCase(entry.getKey())
						|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
					continue;
				}
				try {
					logger.debug(entry.getKey() + "=>" + URLDecoder.decode(entry.getValue()[0], "UTF-8"));
					query.addCriteria(
							Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0], "UTF-8")));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return mongoTemplate.find(query, UserGroupAssociation.class);

	}
	/*
	 * private Set<String> symmetricDifference(Set<String> a, Set<String> b) {
	 * Set<String> result = new HashSet<String>(a); for (String element : b) { //
	 * .add() returns false if element already exists if (!result.add(element)) {
	 * result.remove(element); } } return result; }
	 */
}
