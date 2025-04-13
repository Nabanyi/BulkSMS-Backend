package com.sms.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BulkSmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BulkSmsApplication.class, args);
		System.out.print("App started...");
	}

}
