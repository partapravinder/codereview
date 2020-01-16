package com.newgen.dao;

import java.util.List;
import java.util.Map;

import com.newgen.model.Folder;

public interface FolderDao {
	public Folder findAndRemoveById(String id,String tenantId);

	public Folder findAndRemoveByIdAndVersion(String id, String version,String tenantId);

	public List<Folder> findByParentFolderId(String id,String tenantId);

	public Folder findAndModify(String id, Map<String, String> updateFolderParams, Long version,String tenantId);

	public List<Folder> findAllFolders(Map<String, String[]> paramMap,String tenantId);
	
	public List<Folder> findAllFoldersByPage(Map<String, String[]> paramMap,String tenantId,int Pno);

	public List<Folder> findByFolderName(String folderName,String tenantId);

	public Folder insert(Folder folder);

	public Folder findById(String id,String tenantId);

	public Folder updateParentFolderId(String id, String targetId, Long version,String tenantId);

	public Folder updateChildrenCount(String id, int count,String tenantId);
}
