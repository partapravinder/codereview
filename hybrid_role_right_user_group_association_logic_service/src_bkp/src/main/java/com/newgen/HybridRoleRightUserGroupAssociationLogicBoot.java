package com.newgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class HybridRoleRightUserGroupAssociationLogicBoot {// NOSONAR

	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) throws Exception {
		// System.setProperty("hostip", System.getenv("hostip"));
		SpringApplication.run(HybridRoleRightUserGroupAssociationLogicBoot.class, args);
	}
}
