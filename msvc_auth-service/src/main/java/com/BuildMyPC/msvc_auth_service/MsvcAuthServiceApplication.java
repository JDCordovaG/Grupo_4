package com.BuildMyPC.msvc_auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MsvcAuthServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsvcAuthServiceApplication.class, args);
	}
}