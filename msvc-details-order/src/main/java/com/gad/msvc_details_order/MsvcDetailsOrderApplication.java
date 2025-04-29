package com.gad.msvc_details_order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class MsvcDetailsOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcDetailsOrderApplication.class, args);
	}

}
