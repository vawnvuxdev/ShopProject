package com.evanshop.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.evanshop.common.entity", "com.evanshop.admin.user"})
public class WebBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebBackendApplication.class, args);
	}

}
