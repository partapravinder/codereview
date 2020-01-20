package com.newgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableDiscoveryClient
@SpringBootApplication
public class StorageCredentialsBoot {//NOSONAR

	public static void main(String[] args) throws Exception {
		//System.setProperty("hostip", System.getenv("hostip"));
		SpringApplication.run(StorageCredentialsBoot.class, args);
	}
}
