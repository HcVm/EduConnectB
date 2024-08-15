package com.EduConnectB.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import com.EduConnectB.app.config.CloudinaryProperties;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(CloudinaryProperties.class)
@ComponentScan(basePackages = {"com.EduConnectB.app"})
public class EduConnectBApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduConnectBApplication.class, args);
	}

}