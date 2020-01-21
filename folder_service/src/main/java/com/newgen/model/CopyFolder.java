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

@Document(collection = "copyFolder")
@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CopyFolder implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Status {
		PENDING, IN_PROGRESS, COMPLETED, FAILED
	}

	@Id
	private String id;
	private String sourceFolderId;
	private String targetFolderId;
	private int progress = 0;
	private Status status = Status.PENDING;
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date createdDateTime;
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date statusChangeDateTime;

	public CopyFolder() {
		super();
	}

	public CopyFolder(String sourceFolderId, String targetFolderId,Date createdDateTime) {
		super();
		this.sourceFolderId = sourceFolderId;
		this.targetFolderId = targetFolderId;
		this.createdDateTime = createdDateTime;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CopyFolder [id=" + id + ", sourceFolderId=" + sourceFolderId + ", targetFolderId="
				+ targetFolderId + ", progress=" + progress + ", status=" + status + ", createdDateTime="
				+ createdDateTime + ", statusChangeDateTime=" + statusChangeDateTime + "]";
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