package com.evanshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.evanshop.common.entity.Category;
import com.evanshop.service.CategoryService;

@Controller
public class MainController {

	@Autowired CategoryService categoryService;
	
	@GetMapping("")
	public String homePage(Model model) {
		List<Category> listCategories = categoryService.listNoChildrenCateogries();
		
		model.addAttribute("listCategories", listCategories);
		
		return "index";
	}
}
