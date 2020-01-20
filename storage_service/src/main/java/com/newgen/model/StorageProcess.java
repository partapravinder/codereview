package com.newgen.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection = "storageProcess")
@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorageProcess {
	public enum Status {
		PENDING, IN_PROGRESS, COMPLETED, ACKNOWLEDGED, FAILED
	}

	public enum Action {
		UPLOAD, DELETE, RETRIEVE
	}

	@Id
	private String id;

	private Status status = Status.PENDING;

	private Action action;
	
	@DBRef
	private StorageLocation storageLocation;

	private StoreAction storeAction;

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date creationDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date statusChangeDateTime;

	private String tenantId;
	
	public StorageProcess() {
		super();
	}

	public StorageProcess(String id, Status status, Action action, StoreAction storeAction, Date creationDateTime,String tenantId) {
		super();
		this.id = id;
		this.status = status;
		this.action = action;
		this.storeAction = storeAction;
		this.creationDateTime = creationDateTime;
		this.tenantId=tenantId;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StorageProcess [id=" + id + ", status=" + status + ", action=" + action + ", storageLocation="
				+ storageLocation + ", storeAction=" + storeAction.toString() + ", creationDateTime=" + creationDateTime
				+ ", statusChangeDateTime=" + statusChangeDateTime + ", tenantId=" + tenantId +"]";
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
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * @return the storeAction
	 */
	public StoreAction getStoreAction() {
		return storeAction;
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
	 * @param storeAction
	 *            the storeAction to set
	 */
	public void setStoreAction(StoreAction storeAction) {
		this.storeAction = storeAction;
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
	 * @return the statusChangeDateTime
	 */
	public Date getStatusChangeDateTime() {
		return statusChangeDateTime;
	}

	/**
	 * @param statusChangeDateTime the statusChangeDateTime to set
	 */
	public void setStatusChangeDateTime(Date statusChangeDateTime) {
		this.statusChangeDateTime = statusChangeDateTime;
	}

	/**
	 * @return the storageLocation
	 */
	public StorageLocation getStorageLocation() {
		return storageLocation;
	}

	/**
	 * @param storageLocation the storageLocation to set
	 */
	public void setStorageLocation(StorageLocation storageLocation) {
		this.storageLocation = storageLocation;
	}

}
