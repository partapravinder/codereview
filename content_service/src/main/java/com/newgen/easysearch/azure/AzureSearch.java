package com.newgen.easysearch.azure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.newgen.ecm.azure.search.SearchServiceClient;
import com.newgen.ecm.azure.search.documents.GlobalSearchDocument;
import com.newgen.ecm.constants.ECMConstants;

@Component
public class AzureSearch // implements EasySearch
{

	private static final Logger logger = LoggerFactory.getLogger(AzureSearch.class);

	@Value("${easysearch.azure.indexname:globalsearch}")
	private String indexName;

	@Autowired
	public AzureSearch(@Value("${easysearch.azure.indexname:globalsearch}") String indexName) {
		this.indexName = indexName;
	}

	/*
	 * @Autowired public AzureSearch() { }
	 */

	@Autowired
	SearchServiceClient client;// = new SearchServiceClient();

	public boolean postSearchData(String contentId, String contentName, String contentType, String comments,
			String parentFolderId, String ownerName, String ownerId, String noOfPages, String documentSize,
			String documentType, String tenantId, String metadata) {
		logger.debug("Entering postSearchData()");

		// Search DTO upload
		GlobalSearchDocument searchDto = new GlobalSearchDocument();
		searchDto.setTenantId(tenantId);
		searchDto.setEntityId("D-" + contentId);
		searchDto.setEntityName(contentName);
		searchDto.setEntityOrderByName(contentName);
		searchDto.setEntityDescription(comments);
		Date date = new Date();
		searchDto.setExpectedClosureDateTime(date);
		searchDto.setEntityOwnerId(ownerId);
		searchDto.setEntityCreatorId(ownerId);
		searchDto.setEntityInitiationDateTime(date);
		searchDto.setEntityParentId(parentFolderId);
		searchDto.setEntityParentName(documentSize);
		searchDto.setEntityOwnerUserName(ownerName);
		searchDto.setEntityOwnerFirstName(ownerName);
		searchDto.setEntityOwnerLastName(ownerName);
		searchDto.setEntityCatagory("D");
		searchDto.setTotalTaskCount(noOfPages);
		searchDto.setEntityStatus(contentType);
		searchDto.setTotalCompletedTaskCount("0");

		List<GlobalSearchDocument> value = new ArrayList<>();
		value.add(searchDto);

		logger.debug("Content search metadata mapped to GlobalSearchDocument=>" + new JSONObject(searchDto).toString());

		Boolean result = false;

		try {
			logger.debug("Content being uploaded to Azure Search service");
			logger.debug("indexName==>" + indexName); // Content upload to Azure Search
			result = client.uploadDocument(indexName, value, ECMConstants.WS_Search_Document_MergeOrUpload);
			if (result) {
				logger.debug("Content successfully uploaded to Azure Search service!!!");
			} else {
				logger.error("Failed to upload Search Data!!!");
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		logger.debug("Exit postSearchData()");
		return result;
	}

	public boolean deleteSearchData(String contentId, String tenantId) {
		logger.debug("Entering deleteSearchData()");

		// Search DTO upload
		GlobalSearchDocument searchDto = new GlobalSearchDocument();
		searchDto.setTenantId(tenantId);
		searchDto.setEntityId("D-" + contentId);

		List<GlobalSearchDocument> value = new ArrayList<>();
		value.add(searchDto);

		logger.debug("Content delete metadata mapped to GlobalSearchDocument=>" + new JSONObject(searchDto).toString());

		Boolean result = false;

		try {
			logger.debug("Content being deleted from Azure Search service");
			// Content upload to Azure Search
			logger.debug("indexName" + indexName);
			result = client.uploadDocument(indexName, value, ECMConstants.WS_Search_Document_Delete);
			if (result) {
				logger.debug("Content successfully deleted from Azure Search service!!!");
			} else {
				logger.error("Failed to delete Search Data!!!");
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		logger.debug("Exit postDeleteData()");
		return result;
	}
}
