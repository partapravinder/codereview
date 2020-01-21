package com.newgen.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection = "syncFolderOperation")
@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsyncFolderOperation implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Status {
		PENDING, IN_PROGRESS, COMPLETED, FAILED
	}
	
	public enum Action {
		COPY, DELETE
	}

	@Id
	private String id;
	private Action action;
	private String sourceFolderId;
	private String targetFolderId;
	private int progress = 0;
	private String tenantId;
	private Status status = Status.PENDING;
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date createdDateTime;
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date statusChangeDateTime;

	public AsyncFolderOperation() {
		super();
	}

	public AsyncFolderOperation(Action action, String sourceFolderId, String targetFolderId, Date createdDateTime,String tenantId) {
		super();
		this.action = action;
		this.sourceFolderId = sourceFolderId;
		this.targetFolderId = targetFolderId;
		this.createdDateTime = createdDateTime;
		this.tenantId = tenantId;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AsyncFolderOperation [id=" + id + ", action=" + action + ", sourceFolderId=" + sourceFolderId
				+ ", targetFolderId=" + targetFolderId + ", progress=" + progress + ", status=" + status
				+ ", createdDateTime=" + createdDateTime + ", statusChangeDateTime=" + statusChangeDateTime + ", tenantId=" + tenantId +"]";
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
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the sourceFolderId
	 */
	public String getSourceFolderId() {
		return sourceFolderId;
	}

	/**
	 * @param sourceFolderId
	 *            the sourceFolderId to set
	 */
	public void setSourceFolderId(String sourceFolderId) {
		this.sourceFolderId = sourceFolderId;
	}

	/**
	 * @return the targetFolderId
	 */
	public String getTargetFolderId() {
		return targetFolderId;
	}

	/**
	 * @param targetFolderId
	 *            the targetFolderId to set
	 */
	public void setTargetFolderId(String targetFolderId) {
		this.targetFolderId = targetFolderId;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress
	 *            the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
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
	 * @return the createdDateTime
	 */
	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	/**
	 * @param createdDateTime the createdDateTime to set
	 */
	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
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

}