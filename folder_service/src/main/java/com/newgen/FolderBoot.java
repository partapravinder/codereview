package com.newgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class FolderBoot {//NOSONAR

	public static void main(String[] args) throws Exception {
		//System.setProperty("hostip", System.getenv("hostip"));
		SpringApplication.run(FolderBoot.class, args);
	}

}
