package com.karur.access_management_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class AccessManagementApplication {

	public static void main(String[] args) {
		Hooks.onOperatorDebug();
		SpringApplication.run(AccessManagementApplication.class, args);
	}

}
