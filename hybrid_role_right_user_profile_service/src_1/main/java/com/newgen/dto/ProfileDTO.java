package com.newgen.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDTO {

	@NotBlank(message = "Tenant ID must not be blank!")
	private String objectId;

	@NotBlank(message = "Tenant ID must not be blank!")
	private String objectType;

	@NotBlank(message = "Tenant ID must not be blank!")
	private String rights;

	private boolean favourite;

	public ProfileDTO() {

	}

	public ProfileDTO(String objectId, String objectType, String rights, boolean favourite) {
		this.objectId = objectId;
		this.objectType = objectType;
		this.rights = rights;
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

	public boolean isFavourite() {
		return favourite;
	}

	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}

}
