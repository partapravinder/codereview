package com.newgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableJpaRepositories(basePackages = "com.newgen.repository")
@SpringBootApplication
public class LoggingBoot {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(LoggingBoot.class, args);

	}
}