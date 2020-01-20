package com.newgen.dao.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.newgen.dao.HybridRoleRightGroupUserAssociationDao;
import com.newgen.model.GroupUserAssociation;
import com.newgen.repository.GroupUserAssociationRepository;
import com.newgen.wrapper.service.WrapperMongoService;

@Repository
public class HybridRoleRightGroupUserAssociationDaoImpl implements HybridRoleRightGroupUserAssociationDao {
	private static final Logger logger = LoggerFactory.getLogger(HybridRoleRightGroupUserAssociationDaoImpl.class);

	@Autowired
	GroupUserAssociationRepository groupUserAssociationRepository;

	@Autowired
	WrapperMongoService<GroupUserAssociation> mongoTemplate;

	@Override
	public GroupUserAssociation insert(GroupUserAssociation groupUserAssociation) {
		groupUserAssociation.setCreationDateTime(new Date());
		groupUserAssociation.setAccessDateTime(new Date());
		return groupUserAssociationRepository.insert(groupUserAssociation);
	}

	@Override
	public List<GroupUserAssociation> insertAll(List<GroupUserAssociation> groupUserAssociation) {
		groupUserAssociation.stream().forEach(u -> {
			u.setCreationDateTime(new Date());
			u.setAccessDateTime(new Date());
		});
		return groupUserAssociationRepository.insert(groupUserAssociation);
	}

	@Override
	public List<GroupUserAssociation> saveAll(List<GroupUserAssociation> groupUserAssociation) {
		groupUserAssociation.stream().forEach(u -> {
			u.setCreationDateTime(new Date());
			u.setAccessDateTime(new Date());
		});
		return groupUserAssociationRepository.saveAll(groupUserAssociation);
	}

	@Override
	public GroupUserAssociation save(GroupUserAssociation groupUserAssociation) {
		return groupUserAssociationRepository.save(groupUserAssociation);
	}

	public GroupUserAssociation findById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		GroupUserAssociation groupUserAssociation = (GroupUserAssociation) mongoTemplate.findOne(query,
				GroupUserAssociation.class);
		logger.debug("GroupUserAssociation found : " + groupUserAssociation);
		return groupUserAssociation;
	}

	@Override
	public GroupUserAssociation findByGroup(String group, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("groupId").is(group));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		GroupUserAssociation groupUserAssociation = (GroupUserAssociation) mongoTemplate.findOne(query,
				GroupUserAssociation.class);
		return groupUserAssociation;
	}

	// @SuppressWarnings("unchecked")
	@Override
	public GroupUserAssociation findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		GroupUserAssociation groupUserAssociation = (GroupUserAssociation) mongoTemplate.findAndRemove(query,
				GroupUserAssociation.class);
		logger.debug("Deleted groupUserAssociation : " + groupUserAssociation);
		return groupUserAssociation;
	}

	@Override
	// @SuppressWarnings("unchecked")
	public GroupUserAssociation findAndModify(String id, Map<String, String> updateParams, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(id));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		for (Map.Entry<String, String> entry : updateParams.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
					|| "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			update.set(entry.getKey(), entry.getValue());
		}

		GroupUserAssociation groupUserAssociation = (GroupUserAssociation) mongoTemplate.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true), GroupUserAssociation.class);

		logger.debug("groupUserAssociation : " + groupUserAssociation);
		return groupUserAssociation;
	}

	@Override
	public GroupUserAssociation findAndModifyByGroup(String userId, GroupUserAssociation groupUserAssociation,
			String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("userId").is(userId));

		// JSONObject json = new JSONObject(updateParamsJson);
		// Map<String, Object> map = json.toMap();

		/*
		 * IntStream.range(0,json.length()).forEach(counter->{ if
		 * ("id".equalsIgnoreCase(json.get.getKey()) ||
		 * "version".equalsIgnoreCase(entry.getKey()) ||
		 * "creationDateTime".equalsIgnoreCase(entry.getKey()) ||
		 * "accessDateTime".equalsIgnoreCase(entry.getKey()) ||
		 * "revisedDateTime".equalsIgnoreCase(entry.getKey())) { continue; }
		 * update.set(entry.getKey(), entry.getValue()); });
		 */
		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		/*
		 * for (Map.Entry<String, Object> entry : updateParams.entrySet()) { if
		 * ("id".equalsIgnoreCase(entry.getKey()) ||
		 * "version".equalsIgnoreCase(entry.getKey()) ||
		 * "creationDateTime".equalsIgnoreCase(entry.getKey()) ||
		 * "accessDateTime".equalsIgnoreCase(entry.getKey()) ||
		 * "revisedDateTime".equalsIgnoreCase(entry.getKey())) { continue; }
		 * update.set(entry.getKey(), entry.getValue()); }
		 */
		update.set("groupIds", groupUserAssociation.getGroupIds());
		groupUserAssociation = mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true),
				GroupUserAssociation.class);

		logger.debug("Updated User Profile : " + groupUserAssociation);
		return groupUserAssociation;
	}
	//

	@Override
	public List<GroupUserAssociation> findAll(Map<String, String[]> paramMap, String tenantId) {

		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
					|| "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey()) || "tenantId".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			try {
				logger.debug(entry.getKey() + "=>" + URLDecoder.decode(entry.getValue()[0], "UTF-8"));
				query.addCriteria(Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0], "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return mongoTemplate.find(query, GroupUserAssociation.class);

	}

}
