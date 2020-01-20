package com.newgen.model;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection = "storageLocation")
@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorageLocation {
	
	@Id
	private String id;
	
	private String blobUri;
	
	private String storageCredentialId;
	
	private String containerName;
	
	private String type;
	
	private String tenantId;
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date creationDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date revisedDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date accessDateTime; // not to be updated

	@Version
	private long version;
	
	private Map<String,String> extraFields;

	public StorageLocation() {
		super();
	}

	public StorageLocation(String id, String blobUri, String storageCredentialId,String containerName, String type, Date creationDateTime,
			Date revisedDateTime, Date accessDateTime,String tenantId) {
		super();
		this.id = id;
		this.blobUri = blobUri;
		this.storageCredentialId = storageCredentialId;
		this.containerName = containerName;
		this.type = type;
		this.creationDateTime = creationDateTime;
		this.revisedDateTime = revisedDateTime;
		this.accessDateTime = accessDateTime;
		this.tenantId = tenantId;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StorageLocation [id=" + id + ", blobUri=" + blobUri + ", storageCredentialId=" + storageCredentialId
				+ ", containerName=" + containerName + ", type=" + type + ", creationDateTime=" + creationDateTime + ", revisedDateTime="
				+ revisedDateTime + ", accessDateTime=" + accessDateTime + ", version=" + version + ", extraFields="
				+ extraFields + ", tenantId=" + tenantId +"]";
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
	 * @return the blobUri
	 */
	public String getBlobUri() {
		return blobUri;
	}

	/**
	 * @param blobUri the blobUri to set
	 */
	public void setBlobUri(String blobUri) {
		this.blobUri = blobUri;
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

	/**
	 * @return the creationDateTime
	 */
	public Date getCreationDateTime() {
		return creationDateTime;
	}

	/**
	 * @param creationDateTime the creationDateTime to set
	 */
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	/**
	 * @return the revisedDateTime
	 */
	public Date getRevisedDateTime() {
		return revisedDateTime;
	}

	/**
	 * @param revisedDateTime the revisedDateTime to set
	 */
	public void setRevisedDateTime(Date revisedDateTime) {
		this.revisedDateTime = revisedDateTime;
	}

	/**
	 * @return the accessDateTime
	 */
	public Date getAccessDateTime() {
		return accessDateTime;
	}

	/**
	 * @param accessDateTime the accessDateTime to set
	 */
	public void setAccessDateTime(Date accessDateTime) {
		this.accessDateTime = accessDateTime;
	}

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * @return the extraFields
	 */
	@JsonAnyGetter
	public Map<String, String> getExtraFields() {
		return extraFields;
	}

	/**
	 * @param extraFields the extraFields to set
	 */
	public void setExtraFields(Map<String, String> extraFields) {
		this.extraFields = extraFields;
	}
	
	

}
