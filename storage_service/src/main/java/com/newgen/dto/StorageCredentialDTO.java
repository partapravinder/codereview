package com.newgen.dto;

import javax.validation.constraints.NotEmpty;

public class StorageCredentialDTO {

	@NotEmpty(message = "Name must not be blank!")
	private String name;
	
	private String storageProtocol = "http";

	@NotEmpty(message = "Account name must not be blank!")
	private String accountName;

	@NotEmpty(message = "Account key must not be blank!")
	private String accountKey;

	private String containerName;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * @return the accountKey
	 */
	public String getAccountKey() {
		return accountKey;
	}

	/**
	 * @param accountKey the accountKey to set
	 */
	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}

	/**
	 * @return the storageProtocol
	 */
	public String getStorageProtocol() {
		return storageProtocol;
	}

	/**
	 * @param storageProtocol the storageProtocol to set
	 */
	public void setStorageProtocol(String storageProtocol) {
		this.storageProtocol = storageProtocol;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	
	

}
