package com.newgen.dto;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.newgen.model.Profile;

public class UserProfileDTO {

	// @NotEmpty(message = "tenantId must not be blank!")
	private String tenantId;

	// @NotEmpty(message = "profile must not be blank!")
	private List<Profile> profiles;

	private String userId;

	private List<String> groupIds;

	// private String parentFolderId;

	public UserProfileDTO() {

	}

	public UserProfileDTO(String tenantId, String userId, List<Profile> profiles) {
		this.tenantId = tenantId;
		this.profiles = profiles;
		this.userId = userId;
		// this.parentFolderId = parentFolderId;
	}
	
	public UserProfileDTO(String tenantId, String userId, List<Profile> profiles, List<String> groupIds) {
		this.tenantId = tenantId;
		this.profiles = profiles;
		this.userId = userId;
		this.groupIds = groupIds;
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

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public List<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}

}
