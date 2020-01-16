package com.newgen.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection = "StorageConsumption")
@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorageConsumption {
	
	@Id
	private String id;
	   
	private int noOfUpload;
	
	private int noOfDownload;
	
	private double downloadSize;
	
	private double uploadSize;
  
	private String tenantId;
	
	private String onDate;
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date creationDateTime;
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date revisedDateTime;

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date accessDateTime;
	
 
	
	public StorageConsumption() {
		super();
	}

	public StorageConsumption(String id,String tenantId, int noOfUpload, int noOfDownload, double downloadSize, double uploadSize,String onDate, Date creationDateTime
			) {
		super();
		this.id = id;
		this.noOfUpload = noOfUpload;
		this.noOfDownload = noOfDownload;
		this.downloadSize = downloadSize;
		this.uploadSize = uploadSize; 
		this.onDate = onDate; 
		this.creationDateTime = creationDateTime;
		this.setTenantId(tenantId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Content [id=" + id + ", noOfUpload=" + noOfUpload + ", noOfDownload=" + noOfDownload + ", downloadSize=" + downloadSize
				+ ", uploadSize=" + uploadSize + ", onDate=" + onDate + ", creationDateTime=" + creationDateTime
				+ ", revisedDateTime=" + revisedDateTime + ", accessDateTime=" + accessDateTime + ", tenantId=" + tenantId +"]";
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
	 * @return the name
	 */
	public int getNoOfUpload() {
		return noOfUpload;
	}


	/**
	 * @param name the name to set
	 */
	public void setNoOfUpload(int noOfUpload) {
		this.noOfUpload = noOfUpload;
	}


	/**
	 * @return the contentType
	 */
	public int getNoOfDownload() {
		return noOfDownload;
	}


	/**
	 * @param contentType the contentType to set
	 */
	public void setNoOfDownload(int noOfDownload) {
		this.noOfDownload = noOfDownload;
	}


	/**
	 * @return the comments
	 */
	public double getDownloadSize() {
		return downloadSize;
	}


	/**
	 * @param comments the comments to set
	 */
	public void setDownloadSize(double downloadSize) {
		this.downloadSize = downloadSize;
	}

	/**
	 * @return the parentFolderId
	 */
	public double getUploadSize() {
		return uploadSize;
	}

	/**
	 * @param parentFolderId the parentFolderId to set
	 */
	public void setUploadSize(double uploadSize) {
		this.uploadSize = uploadSize;
	}

	 

	public String getOndate() {
		return onDate;
	}

	/**
	 * @param ownerId
	 *            the ownerId to set
	 */
	public void setOnDate(String onDate) {
		this.onDate = onDate;
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
	 * @return the creationDateTime
	 */
	public Date getCreationDateTime() {
		return creationDateTime;
	}


	/**
	 * @param creationDateTime the creationDateTime to set
	 */
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}


	/**
	 * @return the revisedDateTime
	 */
	public Date getRevisedDateTime() {
		return revisedDateTime;
	}


	/**
	 * @param revisedDateTime the revisedDateTime to set
	 */
	public void setRevisedDateTime(Date revisedDateTime) {
		this.revisedDateTime = revisedDateTime;
	}


	/**
	 * @return the accessDateTime
	 */
	public Date getAccessDateTime() {
		return accessDateTime;
	}


	/**
	 * @param accessDateTime the accessDateTime to set
	 */
	public void setAccessDateTime(Date accessDateTime) {
		this.accessDateTime = accessDateTime;
	}


	/**
	 * @return the version
	 */
	 

}
