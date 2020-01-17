package com.newgen.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newgen.config.RabbitListenerConfig;
import com.newgen.model.AsyncFolderOperation.Status;
import com.newgen.model.CustomMessage;
import com.newgen.service.ContentService;

@Service
public class CustomMessageListener {

	private static final Logger logger = LoggerFactory.getLogger(CustomMessageListener.class);

	@Autowired
	ContentService contentService;

	@RabbitListener(queues = RabbitListenerConfig.QUEUE_SPECIFIC_NAME)
	public void receiveMessage(final CustomMessage customMessage) {
		if (customMessage != null) {
			logger.debug("Received message as specific class:"+ customMessage.toString());
			try {
				if (customMessage.getStatus().equalsIgnoreCase(Status.COMPLETED.toString())) {
					contentService.handleCommitContent(customMessage.getToken(), customMessage.getStorageId(),customMessage.getTenantId());
				} else if (customMessage.getStatus().equalsIgnoreCase(Status.FAILED.toString())) {
					contentService.handleContentRemovalOnFailure(customMessage.getToken(), customMessage.getStorageId(), customMessage.getTenantId());
				}
			} catch (Exception ex) {
				logger.debug(ex.getMessage(), ex);
			}
		} else {
			logger.debug("Message received is null");
		}
	}
}
