package com.newgen.model;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {

	@NotEmpty(message = "objectId must not be blank!")
	private String objectId;

	@NotEmpty(message = "objectId must not be blank!")
	private String objectType;

	private String parentFolderId = "";

	private String rights;
	private boolean favourite;

	public Profile() {

	}

	public Profile(String objectId, String objectType, String parentFolderId) {
		this.objectId = objectId;
		this.objectType = objectType;
		this.parentFolderId = parentFolderId;
	}

	public Profile(String objectId, String objectType, String rights, String parentFolderId, boolean favourite) {
		this.objectId = objectId;
		this.objectType = objectType;
		this.rights = rights;
		this.parentFolderId = parentFolderId;
		this.favourite = favourite;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public String getParentFolderId() {
		return parentFolderId;
	}

	public void setParentFolderId(String parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	public boolean isFavourite() {
		return favourite;
	}

	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}

	@Override
	public int hashCode() {
		return objectId.hashCode();
	}

	@Override
	public boolean equals(Object p) {
		if (p instanceof Profile && ((Profile) p).getObjectId().equals(this.objectId)) {
			return true;
		} else {
			return false;
		}
	}

}
