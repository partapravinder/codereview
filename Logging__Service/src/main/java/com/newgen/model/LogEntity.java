
package com.newgen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "ecmmeterlogdetails", catalog = "dbo")
@DynamicUpdate
@DynamicInsert
public class LogEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long logId;
	private String tenantId;
	private String userId;
	private String logType;
	private String requestType;
	private String serviceType;
	private Double requestPayloadSize;
	private Double responsePayloadSize;
	private Long startTime;
	private Long endTime;
	private Long totalTime;

	@Id
	@Column(name = "logid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	@Column(name = "tenantid")
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Column(name = "userid")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(name = "logtype")
	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	@Column(name = "requesttype")
	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	@Column(name = "servicetype")
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	@Column(name = "requestpayloadsize")
	public Double getRequestPayloadSize() {
		return requestPayloadSize;
	}

	public void setRequestPayloadSize(Double requestPayloadSize) {
		this.requestPayloadSize = requestPayloadSize;
	}

	@Column(name = "responsepayloadsize")
	public Double getResponsePayloadSize() {
		return responsePayloadSize;
	}

	public void setResponsePayloadSize(Double responsePayloadSize) {
		this.responsePayloadSize = responsePayloadSize;
	}

	@Column(name = "starttime")
	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	@Column(name = "endtime")
	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	@Column(name = "totaltime")
	public Long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}

}
