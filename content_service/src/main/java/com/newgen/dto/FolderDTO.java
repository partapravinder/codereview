package com.newgen.dto;

import java.util.Map;

import javax.validation.constraints.NotEmpty;

public class FolderDTO {

	@NotEmpty(message = "Folder name must not be blank!")
	private String folderName;

	private String folderType = "folder";

	private String comments;
	
	private String parentFolderId;

	@NotEmpty(message = "Owner name must not be blank!")
	private String ownerName;

	@NotEmpty(message = "Owner Id must not be blank!")
	private String ownerId;

	private String usedFor = "general";
	
	private String metadataId;

	private Map<String, String> metadata;


	/**
	 * @return the folderName
	 */
	public String getFolderName() {
		return folderName;
	}

	/**
	 * @param folderName the folderName to set
	 */
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	/**
	 * @return the folderType
	 */
	public String getFolderType() {
		return folderType;
	}

	/**
	 * @param folderType the folderType to set
	 */
	public void setFolderType(String folderType) {
		this.folderType = folderType;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the parentFolderId
	 */
	public String getParentFolderId() {
		return parentFolderId;
	}

	/**
	 * @param parentFolderId the parentFolderId to set
	 */
	public void setParentFolderId(String parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	/**
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	/**
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * @return the usedFor
	 */
	public String getUsedFor() {
		return usedFor;
	}

	/**
	 * @param usedFor the usedFor to set
	 */
	public void setUsedFor(String usedFor) {
		this.usedFor = usedFor;
	}

	public String getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(String metadataId) {
		this.metadataId = metadataId;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	
}
