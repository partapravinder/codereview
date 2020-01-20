package com.newgen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.newgen.error.handler.RestResponseErrorHandler;

@Configuration
public class RestConfig {
	
	@Bean
	public RestTemplate createRestTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new RestResponseErrorHandler());
		return restTemplate;
	}
}

