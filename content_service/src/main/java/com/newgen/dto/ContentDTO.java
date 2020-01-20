package com.newgen.dto;

import java.util.Map;

import javax.validation.constraints.NotEmpty;

public class ContentDTO {
	
	@NotEmpty(message = "Name must not be blank!")
	private String name;

	@NotEmpty(message = "Content Type must not be blank!")
	private String contentType;

	private String comments;

	@NotEmpty(message = "ParentFolderId must not be blank!")
	private String parentFolderId;

	@NotEmpty(message = "Owner name must not be blank!")
	private String ownerName;

	@NotEmpty(message = "Owner id must not be blank!")
	private String ownerId;

//	@NotBlank(message = "ContentLocationId must not be blank!")
	private String contentLocationId;
	
	private String token;
	
	private String noOfPages;
	private String documentSize;
	private String documentType;
	private String locationId;

	private String metadataId;
	private Map<String, String> metadata;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
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
	 * @return the contentLocationId
	 */
	public String getContentLocationId() {
		return contentLocationId;
	}

	/**
	 * @param contentLocationId the contentLocationId to set
	 */
	public void setContentLocationId(String contentLocationId) {
		this.contentLocationId = contentLocationId;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the noOfPages
	 */
	public String getNoOfPages() {
		return noOfPages;
	}

	/**
	 * @param noOfPages the noOfPages to set
	 */
	public void setNoOfPages(String noOfPages) {
		this.noOfPages = noOfPages;
	}

	/**
	 * @return the documentSize
	 */
	public String getDocumentSize() {
		return documentSize;
	}

	/**
	 * @param documentSize the documentSize to set
	 */
	public void setDocumentSize(String documentSize) {
		this.documentSize = documentSize;
	}

	/**
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public String getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(String metadataId) {
		this.metadataId = metadataId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	
	
}
