package com.gad.msvc_orders.msvc_orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class MsvcOrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcOrdersApplication.class, args);
	}

}
