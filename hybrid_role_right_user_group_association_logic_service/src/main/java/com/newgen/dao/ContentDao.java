package com.newgen.dao;

import java.util.List;
import java.util.Map;

import com.newgen.model.Content;
import com.newgen.model.InOutParameters;

public interface ContentDao {
	public Content findAndRemoveById(String id,String tenantId);

	public Content findAndRemoveByIdAndVersion(String id, String version,String tenantId);

	public Content findAndModify(String id, String updateContentParams, Long version,String tenantId);
	
	public Content findAndModify(String id,String updateContentParams, Long version, boolean ignoreCommittedFlag, String tenantId);

	public List<Content> findAllContents(Map<String, String[]> paramMap,String tenantId);
	
	public List<Content> findAllContentsByPage(Map<String, String[]> paramMap,String tenantId, int pageNo);
	
	public List<Content> findByParentFolderId(String id,String tenantId);
	
	public List<Content> findByName(String name,String tenantId);
	
    public InOutParameters findOne(String id,String tenantId);
    
    public Content insert(Content content,String tenantId);

	public Content updateParentFolderId(String id, String targetFolderId,Long version,String tenantId);

	public List<Content> findAllDeletedContents();

	public Content findAndRemoveContentLocation(String id,String tenantId);

	public Content findContentWithContentLocation(String id,String tenantId);

	public Content findByToken(String token,String tenantId);

	public Content findAndDeleteByToken(String token,String tenantId);

	public List<Content> findAllContentsByMetadata(String allRequestParams, String tenantId);

	public Content findUncommitedOne(String id, String tenantId);
}
