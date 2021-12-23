package com.urly.urlyservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

@SpringBootApplication(exclude= FlywayAutoConfiguration.class)
public class UrlyServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlyServicesApplication.class, args);
	}

}
