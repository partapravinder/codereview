package com.newgen.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreAction {
	private String contentPath;
	private String storageCredentialId;
	private String containerName;
	private String id;
	private String version;
	private String type;
	
	public StoreAction() {
		super();
	}
	public StoreAction(String contentPath, String storageCredentialId, String containerName, String id,
			String version,String type) {
		super();
		this.contentPath = contentPath;
		this.storageCredentialId = storageCredentialId;
		this.containerName = containerName;
		this.id = id;
		this.version = version;
		this.type = type;
	}
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
	 */
	public String getContainerName() {
		return containerName;
	}
	/**
	 * @param containerName the containerName to set
	 */
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
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
