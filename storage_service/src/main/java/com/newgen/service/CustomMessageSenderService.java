package com.newgen.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newgen.config.RabbitSenderConfig;
import com.newgen.model.CustomMessage;

@Service

public class CustomMessageSenderService {

	private static final Logger logger = LoggerFactory.getLogger(CustomMessageSenderService.class);

	private final RabbitTemplate rabbitTemplate;

	@Autowired
	public CustomMessageSenderService(final RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendMessage(String status, String token, String storageId, String tenantId) {
		final CustomMessage message = new CustomMessage(status, token, storageId, tenantId);
		logger.info("[Token "+token+"] Sending message..." + message.toString());
		rabbitTemplate.convertAndSend(RabbitSenderConfig.EXCHANGE_NAME, RabbitSenderConfig.ROUTING_KEY, message);
	}

}
