package com.evanshop.admin.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evanshop.admin.service.ProductService;
@RestController
public class ProductRestController {

	@Autowired
	private ProductService productService;
	
	@PostMapping("/products/check_unique")
	public String checkUnique(
			@Param("id") Integer id,@Param("name") String name) {
		return productService.checkUnique(id, name);
	}
}
