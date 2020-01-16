package com.newgen;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableDiscoveryClient 
//@EnableEurekaClient
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableScheduling
@EnableRabbit
public class ContentBoot {//NOSONAR
	
	/*
	 * @Value("${content.maxUploadSize}") long content_maxUploadSize;
	 * 
	 * @Bean public MultipartResolver multipartResolver() {
	 * org.springframework.web.multipart.commons.CommonsMultipartResolver
	 * multipartResolver = new
	 * org.springframework.web.multipart.commons.CommonsMultipartResolver();
	 * multipartResolver.setMaxUploadSize(content_maxUploadSize); return
	 * multipartResolver; }
	 * 
	 * @Bean public MultipartConfigElement multipartConfigElement() { return new
	 * MultipartConfigElement(""); }
	 */
	 
	public static void main(String[] args) throws Exception {
		//System.setProperty("hostip", System.getenv("hostip"));
		SpringApplication.run(ContentBoot.class , args);
	}
	
}
