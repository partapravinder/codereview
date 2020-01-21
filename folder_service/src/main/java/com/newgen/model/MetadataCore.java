package com.newgen.model;

import java.util.Date;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataCore {

	private String id; // not to be updated

	private String ownerName; // not to be updated

	private String ownerId; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date creationDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date revisedDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date accessDateTime; // not to be updated
	
	private Map<String, String> data;
	
	public MetadataCore() {
		super();
	}
	
	public MetadataCore(String id) {
		super();
		this.id = id;
	}

	public MetadataCore(String id, String ownerName,
			String ownerId, Date creationDateTime, Date revisedDateTime, Date accessDateTime, Map<String, String> data) {
		super();
		this.id = id;
		this.ownerName = ownerName;
		this.ownerId = ownerId;
		this.creationDateTime = creationDateTime;
		this.revisedDateTime = revisedDateTime;
		this.accessDateTime = accessDateTime;
		this.data= data;
	}

	
	@Override
	public String toString() {
		return "MetadataCore [id=" + id + ", ownerName=" + ownerName + ", ownerId=" + ownerId + ", creationDateTime="
				+ creationDateTime + ", revisedDateTime=" + revisedDateTime + ", accessDateTime=" + accessDateTime
				+ "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public Date getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public Date getRevisedDateTime() {
		return revisedDateTime;
	}

	public void setRevisedDateTime(Date revisedDateTime) {
		this.revisedDateTime = revisedDateTime;
	}

	public Date getAccessDateTime() {
		return accessDateTime;
	}

	public void setAccessDateTime(Date accessDateTime) {
		this.accessDateTime = accessDateTime;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}


}
