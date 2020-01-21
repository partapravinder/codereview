package com.newgen.model;

import java.io.Serializable;

public final class CustomMessage implements Serializable{
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String status;
    private String token;
    private String storageId;
    private String tenantId;
    
    // Default constructor is needed to deserialize JSON
    public CustomMessage() {
    	// Do nothing
    }

	public CustomMessage(String status, String token, String storageId, String tenantId) {
		super();
		this.status = status;
		this.token = token;
		this.storageId = storageId;
		this.tenantId = tenantId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CustomMessage [status=" + status + ", token=" + token + ", storageId=" + storageId + "]";
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the storageId
	 */
	public String getStorageId() {
		return storageId;
	}

	/**
	 * @param storageId the storageId to set
	 */
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
 
    
}