package com.newgen.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.newgen.enumdef.Flag;

@Document(collection = "contentLocation")
@JsonInclude(value = Include.NON_EMPTY)
public class ContentLocation {
	
	@Id
	private String id;
	
	private String locationId;
	
	private int sharedCount;
	

	private String tenantId;
	
	@Version
	private long version;
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date creationDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date revisedDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date accessDateTime; // not to be updated
	
	private Flag flag = Flag.COMMITTED;


	public ContentLocation() {
		super();
	}
	
	public ContentLocation(String id, String locationId, int sharedCount, Date creationDateTime,
			Date revisedDateTime, Date accessDateTime, String tenantId) {
		super();
		this.id = id;
		this.locationId = locationId;
		this.sharedCount = sharedCount;
		this.creationDateTime = creationDateTime;
		this.revisedDateTime = revisedDateTime;
		this.accessDateTime = accessDateTime;
		this.tenantId = tenantId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ContentLocation [id=" + id + ", locationId=" + locationId + ", sharedCount=" + sharedCount
				+ ", version=" + version + ", creationDateTime=" + creationDateTime + ", revisedDateTime="
				+ revisedDateTime + ", accessDateTime=" + accessDateTime + ", tenantId=" + tenantId +"]";
	}

	/**
	 * @return the id
	 */
	
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
	 * @return the locationId
	 */
	public String getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	/**
	 * @return the sharedCount
	 */
	public int getSharedCount() {
		return sharedCount;
	}

	/**
	 * @param sharedCount the sharedCount to set
	 */
	public void setSharedCount(int sharedCount) {
		this.sharedCount = sharedCount;
	}

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(long version) {
		this.version = version;
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
	 * @return the flag
	 */
	public Flag getFlag() {
		return flag;
	}

	/**
	 * @param flag the flag to set
	 */
	public void setFlag(Flag flag) {
		this.flag = flag;
	}
	
	
	
}
