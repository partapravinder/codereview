package com.newgen.easysearch;

public interface EasySearch {
	public boolean postSearchData(String contentId, String contentName, String contentType, String comments,
			String parentFolderId, String ownerName, String ownerId, String noOfPages, String documentSize,
			String documentType, String tenantId, String metadata);
			
	public boolean deleteSearchData(String id, String tenantId);
}
