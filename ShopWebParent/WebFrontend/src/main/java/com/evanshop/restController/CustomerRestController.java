package com.evanshop.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evanshop.service.CustomerService;

@RestController
@RequestMapping("/customers")
public class CustomerRestController {
	
	@Autowired private CustomerService service;

	@PostMapping("/check_email")
	public String checkEmailUniqe(@Param("email") String email) {
		return service.isEmailUnique(email) ? "OK" : "DUPLICATED";
	}
}	
 