package com.newgen.dto;

import java.util.Map;

import javax.validation.constraints.NotEmpty;

public class MetadataDTO {

	@NotEmpty(message = "Owner name must not be blank!")
	private String ownerName;

	@NotEmpty(message = "Owner Id must not be blank!")
	private String ownerId;
	
	@NotEmpty(message = "Metadata must not be blank!")
	private Map<String, String> data;

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
}
