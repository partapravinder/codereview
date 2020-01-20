package com.newgen.request;

public class LogRequest {

	private String userId;
	private String logType;
	private String requestType;
	private String serviceType;
	private Double requestPayloadSize;
	private Double responsePayloadSize;
	private Long startTime;
	private Long endTime;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public Double getRequestPayloadSize() {
		return requestPayloadSize;
	}

	public void setRequestPayloadSize(Double requestPayloadSize) {
		this.requestPayloadSize = requestPayloadSize;
	}

	public Double getResponsePayloadSize() {
		return responsePayloadSize;
	}

	public void setResponsePayloadSize(Double responsePayloadSize) {
		this.responsePayloadSize = responsePayloadSize;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

}
