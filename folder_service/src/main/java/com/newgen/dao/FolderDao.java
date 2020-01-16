package com.newgen.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Query;

import com.newgen.model.Folder;
import com.newgen.model.InOutParameters;

public interface FolderDao {
	public InOutParameters findAndRemoveById(String id, String tenantId);

	public InOutParameters findAndRemoveByIdAndVersion(String id, String version, String tenantId);

	public InOutParameters findByParentFolderId(String id, String tenantId);

	public InOutParameters findAndModify(String id, String updateFolderParams, Long version, String tenantId);

	public InOutParameters findAllFolders(Map<String, String[]> paramMap, String tenantId);

	public InOutParameters findAllFoldersByPage(Map<String, String[]> paramMap, String tenantId, int pno);

	public List<Folder> findByFolderName(String folderName, String tenantId);

	public InOutParameters insert(Folder folder);

	public InOutParameters findById(String id, String tenantId);

	public InOutParameters findCabinetById(String id, String tenantId);

	public InOutParameters updateParentFolderId(String id, String targetId, Long version, String tenantId);

	public Folder updateChildrenCount(String id, int count, String tenantId);

	public InOutParameters findAllContentsByMetadata(String searchParams, String tenantId);

	public InOutParameters findByParentFolderIdInGroup(List<String> userIds, String tenantId);

	public InOutParameters findByPrivateFolderId(List<String> folderIds);

	public InOutParameters getFolderByRevisedDateTime(String id, String tenantId);

}
