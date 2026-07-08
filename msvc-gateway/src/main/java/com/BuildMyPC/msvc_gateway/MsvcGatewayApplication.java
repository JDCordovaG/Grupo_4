package com.BuildMyPC.msvc_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// El gateway es una app Spring Boot normal. Las rutas se definen en application.yml.
// Al tener el cliente Eureka en el classpath, tambien se registra y puede usar lb://.

@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class MsvcGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcGatewayApplication.class, args);
	}

}