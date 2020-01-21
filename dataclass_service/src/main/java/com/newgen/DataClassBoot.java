package com.newgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
//@EnableDiscoveryClient

//@EnableEurekaClient
public class DataClassBoot {// NOSONAR

	public static void main(String[] args) throws Exception {
		// System.setProperty("hostip", System.getenv("hostip"));
		SpringApplication.run(DataClassBoot.class, args);
	}
}