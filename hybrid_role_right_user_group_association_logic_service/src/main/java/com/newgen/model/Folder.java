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
import com.newgen.enumdef.Privilege;

@Document(collection = "folder")
@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Folder {

	@Id
	private String id; // not to be updated

	private String folderName;

	private String folderType; // not to be updated

	private String comments;

	private String parentFolderId;

	private String ownerName; // not to be updated

	private String ownerId; // not to be updated
		
	private String tenantId;

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date creationDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date revisedDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date accessDateTime; // not to be updated

	private String usedFor; // general, reserved, inbox

	@Version
	private long version;

	
	private Privilege privilege; 
	
	private Map<String, String> extraFields;
	
	private Map<String, String> metadata;

	public Folder() {
		super();
	}

	public Folder(String id) {
		super();
		this.id = id;
	}

	public Folder(String id, String folderName, String folderType, String comments, String parentFolderId,
			String ownerName, String ownerId, Date creationDateTime, String usedFor, Map<String, String> metadata,String tenantId) {
		super();
		this.id = id;
		this.folderName = folderName;
		this.folderType = folderType;
		this.comments = comments;
		this.parentFolderId = parentFolderId;
		this.ownerName = ownerName;
		this.ownerId = ownerId;
		this.creationDateTime = creationDateTime;
		this.usedFor = usedFor;
		this.metadata = metadata;
		this.tenantId = tenantId;
	}

	/*public Folder(String id, String folderName, String folderType, String comments, String parentFolderId,
			String ownerName, String ownerId, Date creationDateTime, String usedFor) {
		super();
		this.id = id;
		this.folderName = folderName;
		this.folderType = folderType;
		this.comments = comments;
		this.parentFolderId = parentFolderId;
		this.ownerName = ownerName;
		this.ownerId = ownerId;
		this.creationDateTime = creationDateTime;
		this.usedFor = usedFor;
	}*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Folder [id=" + id + ", folderName=" + folderName + ", folderType=" + folderType + ", comments="
				+ comments + ", parentFolderId=" + parentFolderId + ", ownerName=" + ownerName + ", ownerId=" + ownerId
				+ ", creationDateTime=" + creationDateTime + ", revisedDateTime=" + revisedDateTime
				+ ", accessDateTime=" + accessDateTime + ", usedFor=" + usedFor + ", version=" + version 
				+ ", tenantId=" + tenantId +"]";
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the folderName
	 */
	public String getFolderName() {
		return folderName;
	}

	/**
	 * @param folderName
	 *            the folderName to set
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
	 * @param folderType
	 *            the folderType to set
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
	 * @param comments
	 *            the comments to set
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
	 * @param parentFolderId
	 *            the parentFolderId to set
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
	 * @param ownerName
	 *            the ownerName to set
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
	 * @param ownerId
	 *            the ownerId to set
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param ownerId
	 *            the ownerId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return the creationDateTime
	 */
	public Date getCreationDateTime() {
		return creationDateTime;
	}

	/**
	 * @param creationDateTime
	 *            the creationDateTime to set
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
	 * @param revisedDateTime
	 *            the revisedDateTime to set
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
	 * @param accessDateTime
	 *            the accessDateTime to set
	 */
	public void setAccessDateTime(Date accessDateTime) {
		this.accessDateTime = accessDateTime;
	}

	/**
	 * @return the usedFor
	 */
	public String getUsedFor() {
		return usedFor;
	}

	/**
	 * @param usedFor
	 *            the usedFor to set
	 */
	public void setUsedFor(String usedFor) {
		this.usedFor = usedFor;
	}

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
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
	 * @param extraFields
	 *            the extraFields to set
	 */
	public void setExtraFields(Map<String, String> extraFields) {
		this.extraFields = extraFields;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public Privilege getPrivilege() {
		return privilege;
	}

	public void setPrivilege(Privilege privilege) {
		this.privilege = privilege;
	}
	
	

}

