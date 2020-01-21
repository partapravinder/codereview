package com.newgen.dao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.newgen.model.Content;
import com.newgen.model.Folder;

public interface ContentDao {
	public Content findAndRemoveById(String id, String tenantId);

	public Content findAndRemoveByIdAndVersion(String id, BigDecimal version, String tenantId);

	public Content findAndModify(String id, String updateContentParams, BigDecimal version, String tenantId);

	public Content findAndModify(String id, String updateContentParams, String tenantId);

	public Content findAndCheckOut(String id, String updateContentParams, String tenantId);

	public Content findAndModify(String id, String updateContentParams, BigDecimal version, boolean ignoreCommittedFlag,
			String tenantId, boolean checkout);

	public List<Content> findAllContents(Map<String, String[]> paramMap, String tenantId);

	public List<Content> findAllContentsByPage(Map<String, String[]> paramMap, String tenantId, int pageNo);

	public List<Content> findByParentFolderId(String id, String tenantId);

	public List<Content> findByName(String name, String tenantId);

	public Content findOne(String id, String tenantId);

	public Content findLatestOne(String id, String tenantId);

	public Content insert(Content content, String tenantId);

	public Content updateParentFolderId(String id, String targetFolderId, BigDecimal version, String tenantId);

	public List<Content> findAllDeletedContents();

	public Content findAndRemoveContentLocation(String id, String tenantId);

	public Content findContentWithContentLocation(String id, String tenantId);

	public Content findContentWithPrimaryContentId(String id, BigDecimal version, String tenantId);

	public Content findContentWithPrimaryContentId(String id, String tenantId);

	public Content findByToken(String token, String tenantId);

	public Content findAndDeleteByToken(String token, String tenantId);

	public List<Content> findAllContentsByMetadata(String allRequestParams, String tenantId);

	public List<Content> findAllContentsByDataclass(String allRequestParams, String tenantId);

	public Content findUncommitedOne(String id, String tenantId);

	public List<Content> findAllContentsBySearchString(Map<String, Set<Object>> paramMap, String tenantId,
			boolean ftsEnabled) throws ParseException;

	public List<Folder> findAllFolders(Map<String, Set<Object>> paramMap, String tenantId) throws ParseException;

	public List<Folder> findAllFolders(List<String> folderIds, String tenantId);

	public Content getContentByRevisedDateTime(String id, String tenantId);
}
