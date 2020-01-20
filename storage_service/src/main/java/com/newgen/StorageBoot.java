package com.newgen;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRetry
@EnableCircuitBreaker
@EnableHystrix
@EnableRabbit
//@EnableDiscoveryClient
public class StorageBoot {//NOSONAR

	public static void main(String[] args) throws Exception {
		//System.setProperty("hostip", System.getenv("hostip"));
		SpringApplication.run(StorageBoot.class , args);
	}
	
	
}
