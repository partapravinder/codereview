package com.newgen.dto;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.newgen.model.Profile;

public class BulkGroupProfileDTO {

	// @NotBlank(message = "Tenant ID must not be blank!")
	private String tenantId;

	private List<Profile> profiles;

	private List<GroupProfileDTO> groupIdList;

	private String parentFolderId;

	private String rights;

	public BulkGroupProfileDTO() {

	}

	public BulkGroupProfileDTO(String tenantId, List<GroupProfileDTO> groupIdList, String parentFolderId,
			List<Profile> profiles, String rights) {
		this.tenantId = tenantId;
		this.profiles = profiles;
		this.groupIdList = groupIdList;
		this.parentFolderId = parentFolderId;
		this.rights = rights;
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

	public List<GroupProfileDTO> getGroupIdList() {
		return groupIdList;
	}

	public void setGroupIdList(List<GroupProfileDTO> groupIdList) {
		this.groupIdList = groupIdList;
	}

	public String getParentFolderId() {
		return parentFolderId;
	}

	public void setParentFolderId(String parentFolderId) {
		this.parentFolderId = parentFolderId;
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

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

}
