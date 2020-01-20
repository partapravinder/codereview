package com.newgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HybridRoleRightUserGroupProfileBoot {//NOSONAR

	public static void main(String[] args) throws Exception {
		//System.setProperty("hostip", System.getenv("hostip"));
		SpringApplication.run(HybridRoleRightUserGroupProfileBoot.class, args);
	}
}
