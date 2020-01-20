package com.newgen.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection = "lock")
@JsonInclude(value = Include.NON_EMPTY)
public class Lock {

	@Id
	private String id;
	
	private String appId;
	

	private String tenantId;
	
	@Indexed(expireAfterSeconds = 120)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date _ts;
	
	private String lockType = "exclusive";
	
	private int sharedCount;

	public Lock(){
		super();
	}

	public Lock(String id, String appId, Date ts,String tenantId) {
		super();
		this.id = id;
		this.appId = appId;
		this._ts = ts;
		this.tenantId = tenantId;
	}



	public Lock(String id, String appId, Date ts, String lockType, int sharedCount,String tenantId) {
		super();
		this.id = id;
		this.appId = appId;
		this._ts = ts;
		this.lockType = lockType;
		this.sharedCount = sharedCount;
		this.tenantId = tenantId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Lock [id=" + id + ", appId=" + appId + ", ts=" + _ts + ", lockType=" + lockType + ", sharedCount="
				+ sharedCount + ", tenantId=" + tenantId +"]";
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
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the ts
	 */
	public Date getTs() {
		return _ts;
	}

	/**
	 * @param ts the ts to set
	 */
	public void setTs(Date ts) {
		this._ts = ts;
	}

	/**
	 * @return the lockType
	 */
	public String getLockType() {
		return lockType;
	}

	/**
	 * @param lockType the lockType to set
	 */
	public void setLockType(String lockType) {
		this.lockType = lockType;
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

}
