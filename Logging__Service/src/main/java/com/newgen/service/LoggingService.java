
package com.newgen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newgen.model.LogEntity;
import com.newgen.repository.LoggingRepository;
import com.newgen.request.LogRequest;

@Service
public class LoggingService {

	/*
	 * @Autowired private LoggingDao loggingDao;
	 */

	@Autowired
	LoggingRepository loggingRepository;

	public String saveLogs(String tenantId, LogRequest logRequest) {
		LogEntity logEntity = new LogEntity();
		Long totalTime = null;
		logEntity.setTenantId(tenantId);
		logEntity.setLogType(logRequest.getLogType());
		logEntity.setRequestType(logRequest.getRequestType());
		logEntity.setServiceType(logRequest.getServiceType());
		logEntity.setRequestPayloadSize(logRequest.getRequestPayloadSize());
		logEntity.setResponsePayloadSize(logRequest.getResponsePayloadSize());
		logEntity.setUserId(logRequest.getUserId());
		logEntity.setStartTime(logRequest.getStartTime());
		logEntity.setEndTime(logRequest.getEndTime());
		if (logRequest.getStartTime() != null && logRequest.getEndTime() != null) {
			totalTime = logRequest.getEndTime() - logRequest.getStartTime();
		}
		logEntity.setTotalTime(totalTime);
		try {
			loggingRepository.save(logEntity);
		} catch (Exception e) {
			return "false";
		}
		return "true";
	}
}
