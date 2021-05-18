package com.evanshop.admin.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evanshop.admin.service.CategoryService;

@RestController
public class CategoryRestController {
	
	@Autowired
	private CategoryService catgService;
	
	@PostMapping("/categories/check_unique")
	public String checkUnique(
			@Param("id") Integer id,@Param("name") String name,@Param("alias") String alias) {
		return catgService.checkUnique(id, name, alias);
	}
}
