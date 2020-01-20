package com.newgen.dao.impl;

import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.HybridRoleRightGroupProfileDao;
import com.newgen.dao.HybridRoleRightGroupUserAssociationDao;
import com.newgen.exception.CustomException;
import com.newgen.model.GroupProfile;
import com.newgen.model.GroupUserAssociation;
import com.newgen.repository.GroupProfileRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class HybridRoleRightGroupProfileDaoImpl implements HybridRoleRightGroupProfileDao {
	private static final Logger logger = LoggerFactory.getLogger(HybridRoleRightGroupProfileDaoImpl.class);

	@Autowired
	GroupProfileRepository groupProfileRepository;

	@Autowired
	WrapperMongoService<GroupProfile> mongoTemplate;

	@Autowired
	ExceptionThrower exceptionThrower;

	@Autowired
	HybridRoleRightGroupUserAssociationDao hybridRoleRightGroupUserAssociationDao;

	@Override
	public GroupProfile insert(GroupProfile groupProfile) {
		groupProfile.setCreationDateTime(new Date());
		groupProfile.setAccessDateTime(new Date());
		return groupProfileRepository.insert(groupProfile);
	}

	@Override
	public List<GroupProfile> insertAll(List<GroupProfile> groupProfile) {
		groupProfile.stream().forEach(u -> {
			u.setCreationDateTime(new Date());
			u.setAccessDateTime(new Date());
		});
		return groupProfileRepository.insert(groupProfile);
	}

	@Override
	public List<GroupProfile> saveAll(List<GroupProfile> groupProfile) {
		groupProfile.stream().forEach(u -> {
			u.setCreationDateTime(new Date());
			u.setAccessDateTime(new Date());
		});
		return groupProfileRepository.saveAll(groupProfile);
	}

	@Override
	public GroupProfile save(GroupProfile groupProfile) {
		return groupProfileRepository.save(groupProfile);
	}

	public GroupProfile findById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		GroupProfile groupProfile = (GroupProfile) mongoTemplate.findOne(query, GroupProfile.class);
		logger.debug("GroupProfile found : " + groupProfile);
		return groupProfile;
	}

	@Override
	public GroupProfile findByGroup(String group, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("groupId").is(group));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		GroupProfile groupProfile = (GroupProfile) mongoTemplate.findOne(query, GroupProfile.class);
		return groupProfile;
	}

	// @SuppressWarnings("unchecked")
	@Override
	public GroupProfile findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		GroupProfile groupProfile = (GroupProfile) mongoTemplate.findAndRemove(query, GroupProfile.class);
		logger.debug("Deleted groupProfile : " + groupProfile);
		return groupProfile;
	}

	@Override
	// @SuppressWarnings("unchecked")
	public GroupProfile findAndModify(String id, Map<String, Object> updateParams, String tenantId) {
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
			update.set(entry.getKey(), entry.getValue());
		}

		GroupProfile groupProfile = (GroupProfile) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), GroupProfile.class);

		logger.debug("groupProfile : " + groupProfile);
		return groupProfile;
	}

	@Override
	@SuppressWarnings("unchecked")
	public GroupProfile findAndModifyByGroup(String updateParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> updateParamsMap = mapper.readValue(updateParams, Map.class);

		Query query = new Query();
		String groupId = updateParamsMap.get("groupId").toString();
		if (groupId == null || groupId.isEmpty()) {
			//TODO Throw Exception Object does not exist.
		}

		query.addCriteria(Criteria.where("groupId").is(groupId));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		Set<Object> updateduserids = new HashSet<Object>();
		Set<Object> existinguserids = new HashSet<Object>();

		GroupProfile groupProfileExisting = findById(groupId, tenantId);

		if (groupProfileExisting == null) {
			System.out.println("Group Profile not found!!!!!");
			// Throw Exception that record not found
		}

		if (groupProfileExisting.getUserIds() != null) {
			System.out.println(groupProfileExisting.getUserIds().size());
			existinguserids.addAll(groupProfileExisting.getUserIds());
			System.out.println(existinguserids.size());
		}
		if (updateParams != null) {
			for (Map.Entry<String, Object> entry : updateParamsMap.entrySet()) {

				System.out.println(entry.getKey() + "---" + entry.getValue());
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
						} else {

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					update.set(entry.getKey(), entry.getValue());
				}
			}
		}

		GroupProfile groupProfile = (GroupProfile) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), GroupProfile.class);

		logger.debug("Updated Group Profile : " + groupProfile);
		return groupProfile;
	}
	//

	@Override
	// @SuppressWarnings("unchecked")
	public List<GroupProfile> findAll(Map<String, String[]> paramMap, String tenantId) {

		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
					|| "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())
					|| "tenantId".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			try {
				logger.debug(entry.getKey() + "=>" + URLDecoder.decode(entry.getValue()[0], "UTF-8"));
				// query.addCriteria(Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0],
				// "UTF-8")));
				if (entry.getValue().length == 1) {
					query.addCriteria(
							Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0], "UTF-8")));
				} else if (entry.getValue().length > 1) {
					query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return mongoTemplate.find(query, GroupProfile.class);

	}

	@Override
	@SuppressWarnings("unchecked")
	public GroupProfile findAndDeallocateUserFromGroupId(String tenantId, String groupId, String[] userIds)
			throws CustomException {
		Map<String, Object> updateParams = new HashMap<String, Object>();
		// updateParams.put("userIds", userId);

		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(groupId));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		Set<Object> existinguserids = new HashSet<Object>();

		GroupProfile existingGroupProfile = findById(groupId, tenantId);
		if (existingGroupProfile == null) {
			exceptionThrower.throwGroupIdNotPresent();
		}
		existinguserids.addAll(existingGroupProfile.getUserIds());

		if (existinguserids.size() > 0) {
			int originalSize = existinguserids.size();

			Arrays.asList(userIds).forEach(userId -> {
				if (existinguserids.contains(userId)) {
					existinguserids.remove(userId);
				}
			});

			if (originalSize == existinguserids.size()) {
				exceptionThrower.throwUserIdNotPresentInThisGroup();
			}
		} else {
			exceptionThrower.throwNoUserIdNotPresentInThisGroup();
		}

		updateParams.put("userIds", existinguserids);

		if (updateParams != null) {
			for (Map.Entry<String, Object> entry : updateParams.entrySet()) {
				if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
						|| "creationDateTime".equalsIgnoreCase(entry.getKey())
						|| "accessDateTime".equalsIgnoreCase(entry.getKey())
						|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
					continue;
				}
				update.set(entry.getKey(), entry.getValue());
			}
		}
		GroupProfile userGroupProfileAssociation = (GroupProfile) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), GroupProfile.class);

		/*
		 * if (existinguserids.size() > 0) { for (Object user : existinguserids) {
		 * GroupUserAssociation groupUserAssociation =
		 * hybridRoleRightGroupUserAssociationDao .findById(user.toString(), tenantId);
		 * if (groupUserAssociation != null) {
		 * 
		 * groupUserAssociation.getGroupIds().remove(groupId);
		 * 
		 * hybridRoleRightGroupUserAssociationDao.findAndModifyByGroup(user.toString(),
		 * groupUserAssociation, tenantId); } } }
		 */

		return userGroupProfileAssociation;
	}

}
