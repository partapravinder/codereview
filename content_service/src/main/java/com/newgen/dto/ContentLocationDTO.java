package com.newgen.dto;

import javax.validation.constraints.NotEmpty;

public class ContentLocationDTO {
	
	@NotEmpty(message = "StorageId must not be blank!")
	private String storageId;

	public String getStorageId() {
		return storageId;
	}

	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}
	
}
