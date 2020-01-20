package com.newgen.dto;

import javax.validation.constraints.NotEmpty;

public class StoreContentDTO {

	@NotEmpty(message = "Content path must not be blank!")
	private String contentPath;
	
	@NotEmpty(message = "Storage Credential Id must not be blank!")
	private String storageCredentialId;
	
	//@NotEmpty(message = "Container Name must not be blank!")
	//private String containerName;
	
	@NotEmpty(message = "Storage type must not be blank!")
	private String type;

	/**
	 * @return the contentPath
	 */
	public String getContentPath() {
		return contentPath;
	}

	/**
	 * @param contentPath the contentPath to set
	 */
	public void setContentPath(String contentPath) {
		this.contentPath = contentPath;
	}

	/**
	 * @return the storageCredentialId
	 */
	public String getStorageCredentialId() {
		return storageCredentialId;
	}

	/**
	 * @param storageCredentialId the storageCredentialId to set
	 */
	public void setStorageCredentialId(String storageCredentialId) {
		this.storageCredentialId = storageCredentialId;
	}

	/**
	 * @return the containerName
	 *//*
	public String getContainerName() {
		return containerName;
	}

	*//**
	 * @param containerName the containerName to set
	 *//*
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}*/

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
