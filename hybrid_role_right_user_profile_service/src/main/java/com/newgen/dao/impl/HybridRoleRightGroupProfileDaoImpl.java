package com.newgen.dao.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.comparator.ProfileComparator;
import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.HybridRoleRightGroupProfileDao;
import com.newgen.dao.HybridRoleRightGroupUserAssociationDao;
import com.newgen.exception.CustomException;
import com.newgen.model.GroupProfile;
import com.newgen.model.InOutParameters;
import com.newgen.model.Profile;
import com.newgen.repository.GroupProfileRepository;
import com.newgen.wrapper.service.WrapperMongoService;
import com.newgen.wrapper.service.impl.WrapperServiceImpl;

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

	@Autowired
	WrapperServiceImpl wrapperServiceImpl;

	@Override
	public GroupProfile insert(GroupProfile groupProfile) {
		groupProfile.setCreationDateTime(new Date());
		groupProfile.setAccessDateTime(new Date());
		try {
			return groupProfileRepository.insert(groupProfile);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return groupProfileRepository.insert(groupProfile);
		}
	}

	@Override
	public List<GroupProfile> insertAll(List<GroupProfile> groupProfile) {
		groupProfile.stream().forEach(u -> {
			u.setCreationDateTime(new Date());
			u.setAccessDateTime(new Date());
		});
		try {
			return groupProfileRepository.insert(groupProfile);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			return groupProfileRepository.insert(groupProfile);
		}
	}

	@Override
	public InOutParameters saveAll(List<GroupProfile> groupProfile) {
		List<GroupProfile> groupProfileList = null;
		InOutParameters inOutParameters = new InOutParameters();
		Double resSize = 0.0;
		groupProfile.stream().forEach(u -> {
			u.setCreationDateTime(new Date());
			u.setAccessDateTime(new Date());
		});
		try {
			groupProfileList = groupProfileRepository.saveAll(groupProfile);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			groupProfileList = groupProfileRepository.saveAll(groupProfile);
		}
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(groupProfile).totalSize()) / 1024.0);
		if (groupProfileList != null) {
			resSize = (GraphLayout.parseInstance(groupProfileList).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setGroupProfileList(groupProfileList);
		return inOutParameters;
	}

	@Override
	public InOutParameters save(GroupProfile groupProfile) {
		GroupProfile groupProfile2 = new GroupProfile();
		Double resSize = 0.0;
		InOutParameters inOutParameters = new InOutParameters();
		try {
			groupProfile2 = groupProfileRepository.save(groupProfile);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			groupProfile2 = groupProfileRepository.save(groupProfile);
		}
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(groupProfile).totalSize()) / 1024.0);
		if (groupProfile2 != null) {
			resSize = (GraphLayout.parseInstance(groupProfile2).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setResponsePayloadSize((GraphLayout.parseInstance(groupProfile2).totalSize()) / 1024.0);
		inOutParameters.setGroupProfile(groupProfile2);
		return inOutParameters;
	}

	public InOutParameters findById(String id, String tenantId) {
		Query query = new Query();
		Double resSize = 0.0;
		InOutParameters inOutParameters = new InOutParameters();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		GroupProfile groupProfile = null;
		try {
			groupProfile = (GroupProfile) mongoTemplate.findOne(query, GroupProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (groupProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				groupProfile = (GroupProfile) mongoTemplate.findOne(query, GroupProfile.class);
			}
			logger.debug(" " + groupProfile);
		}
		logger.debug("GroupProfile found : " + groupProfile);
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		if (groupProfile != null) {
			resSize = (GraphLayout.parseInstance(groupProfile).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setGroupProfile(groupProfile);
		return inOutParameters;
	}

	@Override
	public InOutParameters findByGroup(String group, String tenantId) {
		Query query = new Query();
		Double resSize = 0.0;
		InOutParameters inOutParameters = new InOutParameters();
		query.addCriteria(Criteria.where("groupId").is(group));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		GroupProfile groupProfile = null;
		try {
			groupProfile = (GroupProfile) mongoTemplate.findOne(query, GroupProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (groupProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				groupProfile = (GroupProfile) mongoTemplate.findOne(query, GroupProfile.class);
			}
			logger.debug(" " + groupProfile);
		}
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		if (groupProfile != null) {
			resSize = (GraphLayout.parseInstance(groupProfile).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setGroupProfile(groupProfile);
		return inOutParameters;
	}

	// @SuppressWarnings("unchecked")
	@Override
	public InOutParameters findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		GroupProfile groupProfile = null;
		try {
			groupProfile = (GroupProfile) mongoTemplate.findAndRemove(query, GroupProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (groupProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				groupProfile = (GroupProfile) mongoTemplate.findAndRemove(query, GroupProfile.class);
			}
			logger.debug(" " + groupProfile);
		}
		logger.debug("Deleted groupProfile : " + groupProfile);
		Double resSize = 0.0;
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		if (groupProfile != null) {
			resSize = (GraphLayout.parseInstance(groupProfile).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setGroupProfile(groupProfile);
		return inOutParameters;
	}

	@Override
	// @SuppressWarnings("unchecked")
	public InOutParameters findAndModify(String id, Map<String, Object> updateParams, String tenantId) {
		Query query = new Query();
		Double resSize = 0.0;
		InOutParameters inOutParameters = new InOutParameters();
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

		GroupProfile groupProfile = null;
		try {
			groupProfile = (GroupProfile) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), GroupProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (groupProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				groupProfile = (GroupProfile) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), GroupProfile.class);
			}
			logger.debug(" " + groupProfile);
		}

		logger.debug("groupProfile : " + groupProfile);
		inOutParameters.setRequestPayloadSize(((GraphLayout.parseInstance(query).totalSize()) / 1024.0)
				+ ((GraphLayout.parseInstance(update).totalSize()) / 1024.0));
		if (groupProfile != null) {
			resSize = (GraphLayout.parseInstance(groupProfile).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setGroupProfile(groupProfile);
		return inOutParameters;
	}

	@Override
	@SuppressWarnings("unchecked")
	public InOutParameters findAndModifyByGroup(String updateParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		InOutParameters inOutParamResult = new InOutParameters();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> updateParamsMap = mapper.readValue(updateParams, Map.class);

		Query query = new Query();
		String groupId = updateParamsMap.get("groupId").toString();
		if (groupId == null || groupId.isEmpty()) {
			// TODO Throw Exception Object does not exist.
		}

		query.addCriteria(Criteria.where("groupId").is(groupId));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		if (updateParams != null) {
			JSONObject updatedParamsJson = new JSONObject(updateParams);
			ObjectMapper obj_mapper = new ObjectMapper();

			// Handle profile
			if (updatedParamsJson.has("profiles") && updatedParamsJson.get("profiles") != null) {
				List<Map<String, String>> profilelist = null;

				Map<String, Map<String, String>> profilemap = new HashMap<String, Map<String, String>>();
				try {
					// String te = updatedParamsJson.get("profiles").toString();
					profilelist = obj_mapper.readValue(updatedParamsJson.get("profiles").toString(),
							(new ArrayList<Map<String, String>>()).getClass());

					profilelist.stream().forEach(pro -> {
						profilemap.put(pro.get("objectId"), pro);
					});

				} catch (IOException e) {
					e.printStackTrace();
				}
				Set<Profile> updatedprofileset = new HashSet<Profile>();
				for (Map.Entry<String, Map<String, String>> entry : profilemap.entrySet()) {
					// updatedprofilelist.add(entry.getValue());
					Profile p = obj_mapper.convertValue(entry.getValue(), Profile.class);
					updatedprofileset.add(p);
				}
				// Get existing
				long sT = System.nanoTime();
				InOutParameters inOutParams = findById(groupId, tenantId);
				long eT = System.nanoTime();
				wrapperServiceImpl.callLoggingService(tenantId, null, "CosmosDB", sT, eT,
						inOutParams.getRequestPayloadSize(), inOutParams.getResponsePayloadSize(), "GET",
						"UserGroupService");

				GroupProfile profile_temp = inOutParams.getGroupProfile();
				if (profile_temp.getProfiles() != null) {// If already profile exists, append new map
					List<Profile> Existing_profile = profile_temp.getProfiles();
					TreeSet<Profile> existingSet = new TreeSet<Profile>(new ProfileComparator());
					existingSet.addAll(updatedprofileset);
					existingSet.addAll(Existing_profile);
					/*
					 * Existing_profile.stream().forEach(p -> { profilemap.put(p.getObjectId(), p);
					 * });
					 */

					update.set("profiles", existingSet);
				} else {// add new
					update.set("profiles", updatedprofileset);
				}
				updatedParamsJson.remove("profiles");
			}

			Set<String> existingUserIds = new HashSet<String>();

			if (updatedParamsJson.has("userIds") && updatedParamsJson.get("userIds") != null) {

				long sT = System.nanoTime();
				InOutParameters inOutParams = findById(groupId, tenantId);
				long eT = System.nanoTime();
				wrapperServiceImpl.callLoggingService(tenantId, null, "CosmosDB", sT, eT,
						inOutParams.getRequestPayloadSize(), inOutParams.getResponsePayloadSize(), "GET",
						"UserGroupService");

				GroupProfile existingGroupProfile = inOutParams.getGroupProfile();
				if (existingGroupProfile != null) {
					if (existingGroupProfile.getUserIds() != null) {
						existingUserIds.addAll(existingGroupProfile.getUserIds());
					}
				}
				List<String> UserList;
				UserList = obj_mapper.readValue(updatedParamsJson.get("userIds").toString(), List.class);
				existingUserIds.addAll(UserList);
				update.set("userIds", existingUserIds);
				updatedParamsJson.remove("userIds");
			}

			Map<String, String> userprofileMap = null;
			try {
				userprofileMap = obj_mapper.readValue(updatedParamsJson.toString(), Map.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (Map.Entry<String, String> entry : userprofileMap.entrySet()) {
				if ("id".equalsIgnoreCase(entry.getKey()) || "creationDateTime".equalsIgnoreCase(entry.getKey())
						|| "accessDateTime".equalsIgnoreCase(entry.getKey())
						|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
					continue;
				}
				update.set(entry.getKey(), entry.getValue());
			}
		}

		GroupProfile groupProfile = null;
		try {
			groupProfile = (GroupProfile) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), GroupProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (groupProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				groupProfile = (GroupProfile) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), GroupProfile.class);
			}
			logger.debug(" " + groupProfile);
		}

		Double resSize = 0.0;
		logger.debug("Updated Group Profile : " + groupProfile);
		inOutParamResult.setRequestPayloadSize(((GraphLayout.parseInstance(query).totalSize()) / 1024.0)
				+ ((GraphLayout.parseInstance(update).totalSize()) / 1024.0));
		if (groupProfile != null) {
			resSize = (GraphLayout.parseInstance(groupProfile).totalSize()) / 1024.0;
		}
		inOutParamResult.setResponsePayloadSize(resSize);
		inOutParamResult.setGroupProfile(groupProfile);
		return inOutParamResult;
	}
	//

	@Override
	// @SuppressWarnings("unchecked")
	public InOutParameters findAll(Map<String, String[]> paramMap, String tenantId) {

		Query query = new Query();
		InOutParameters inOutParamResult = new InOutParameters();
		List<GroupProfile> groupProfileList = null;
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
		try {
			groupProfileList = mongoTemplate.find(query, GroupProfile.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			groupProfileList = mongoTemplate.find(query, GroupProfile.class);
		}
		Double resSize = 0.0;
		inOutParamResult.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		if (groupProfileList != null) {
			resSize = (GraphLayout.parseInstance(groupProfileList).totalSize()) / 1024.0;
		}
		inOutParamResult.setResponsePayloadSize(resSize);
		inOutParamResult.setGroupProfileList(groupProfileList);
		return inOutParamResult;
	}

	@Override
	public InOutParameters findAndDeallocateUserFromGroupId(String tenantId, String groupId, String[] userIds)
			throws CustomException {
		Map<String, Object> updateParams = new HashMap<String, Object>();
		InOutParameters inOutParamResult = new InOutParameters();
		// updateParams.put("userIds", userId);

		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(groupId));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		Set<Object> existinguserids = new HashSet<Object>();

		long sT = System.nanoTime();
		InOutParameters inOutParams = findById(groupId, tenantId);
		long eT = System.nanoTime();
		wrapperServiceImpl.callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");

		GroupProfile existingGroupProfile = inOutParams.getGroupProfile();
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
		GroupProfile userGroupProfileAssociation = null;
		try {
			userGroupProfileAssociation = (GroupProfile) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), GroupProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userGroupProfileAssociation == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userGroupProfileAssociation = (GroupProfile) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), GroupProfile.class);
			}
			logger.debug(" " + userGroupProfileAssociation);
		}

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

		Double resSize = 0.0;
		inOutParamResult.setRequestPayloadSize(((GraphLayout.parseInstance(query).totalSize()) / 1024.0)
				+ ((GraphLayout.parseInstance(update).totalSize()) / 1024.0));
		if (userGroupProfileAssociation != null) {
			resSize = (GraphLayout.parseInstance(userGroupProfileAssociation).totalSize()) / 1024.0;
		}
		inOutParamResult.setResponsePayloadSize(resSize);
		inOutParamResult.setGroupProfile(userGroupProfileAssociation);
		return inOutParamResult;
	}

	@Override
	public InOutParameters findAndDeallocateProfileFromGroupId(String tenantId, String groupId, String[] objectIds)
			throws CustomException {
		InOutParameters inOutParamResult = new InOutParameters();
		Map<String, Object> updateParams = new HashMap<String, Object>();
		// updateParams.put("userIds", userId);

		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(groupId));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		long sT = System.nanoTime();
		InOutParameters inOutParams = findById(groupId, tenantId);
		long eT = System.nanoTime();
		wrapperServiceImpl.callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");

		GroupProfile existingGroupProfile = inOutParams.getGroupProfile();
		if (existingGroupProfile == null) {
			exceptionThrower.throwGroupIdNotPresent();
		}

		Set<Profile> existingProfiles = new HashSet<Profile>(existingGroupProfile.getProfiles());

		Set<Profile> removeProfiles = new HashSet<Profile>();

		// existinguserids.addAll(existingGroupProfile.getUserIds());

		if (existingProfiles.size() > 0) {
			int originalSize = existingProfiles.size();

			Arrays.asList(objectIds).forEach(objectId -> {
				existingProfiles.forEach(profile -> {
					if (profile.getObjectId().equals(objectId)) {
						removeProfiles.add(profile);
					}
				});

			});

			existingProfiles.removeAll(removeProfiles);
			if (originalSize == existingProfiles.size()) {
				exceptionThrower.throwUserIdNotPresentInThisGroup();
			}
		} else {
			exceptionThrower.throwNoUserIdNotPresentInThisGroup();
		}

		updateParams.put("profiles", existingProfiles);

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
		GroupProfile groupProfile = null;
		try {
			groupProfile = (GroupProfile) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), GroupProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (groupProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				groupProfile = (GroupProfile) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), GroupProfile.class);
			}
			logger.debug(" " + groupProfile);
		}

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
		Double resSize = 0.0;
		inOutParamResult.setRequestPayloadSize(((GraphLayout.parseInstance(query).totalSize()) / 1024.0)
				+ ((GraphLayout.parseInstance(update).totalSize()) / 1024.0));
		if (groupProfile != null) {
			resSize = (GraphLayout.parseInstance(groupProfile).totalSize()) / 1024.0;
		}
		inOutParamResult.setResponsePayloadSize(resSize);
		inOutParamResult.setGroupProfile(groupProfile);
		return inOutParamResult;
	}

}
