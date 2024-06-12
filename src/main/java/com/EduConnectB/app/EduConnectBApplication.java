package com.EduConnectB.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.EduConnectB.app"})
public class EduConnectBApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduConnectBApplication.class, args);
	}

}
