package com.newgen.dto;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class UserGroupAssociationDTO {

	@NotEmpty(message = "tenantId must not be blank!")
	private String tenantId;

	@NotEmpty(message = "profile must not be blank!")
	private List<String> userIds;

	private String assocId;

	private String groupId;

	// private String parentFolderId;

	public UserGroupAssociationDTO() {

	}

	public UserGroupAssociationDTO(String tenantId, String groupId, List<String> userIds) {
		this.tenantId = tenantId;
		this.groupId = groupId;
		this.userIds = userIds;
		// this.parentFolderId = parentFolderId;
	}

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date creationDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date revisedDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date accessDateTime; // not to be updated

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Date getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public Date getRevisedDateTime() {
		return revisedDateTime;
	}

	public void setRevisedDateTime(Date revisedDateTime) {
		this.revisedDateTime = revisedDateTime;
	}

	public Date getAccessDateTime() {
		return accessDateTime;
	}

	public void setAccessDateTime(Date accessDateTime) {
		this.accessDateTime = accessDateTime;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public String getAssocId() {
		return assocId;
	}

	public void setAssocId(String assocId) {
		this.assocId = assocId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
