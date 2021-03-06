package com.evanshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.evanshop.common.entity.Category;
import com.evanshop.common.entity.Product;
import com.evanshop.common.exception.CategoryNotFoundException;
import com.evanshop.common.exception.ProductNotFoundException;
import com.evanshop.service.CategoryService;
import com.evanshop.service.ProductService;

@Controller
public class ProductController {

	@Autowired
	CategoryService categoryService;
	@Autowired
	ProductService productService;

	@GetMapping("/category/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
		return viewCategoryByPage(alias, 1, model);
	}

	@GetMapping("/category/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias, @PathVariable("pageNum") int pageNum,
			Model model) {
		try {
			Category categoryByAlias = categoryService.getCategoryByAlias(alias);
			List<Category> listCategoryParents = categoryService.getCategoryParents(categoryByAlias);
			Page<Product> pageProducts = productService.listProductsByCategory(pageNum, categoryByAlias.getId());
			List<Product> listProducts = pageProducts.getContent();

			long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
			long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;

			if (endCount > pageProducts.getTotalElements()) {
				endCount = pageProducts.getTotalElements();
			}

			model.addAttribute("pageTitle", categoryByAlias.getName());
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("totalPages", pageProducts.getTotalPages());
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalItems", pageProducts.getTotalElements());
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("category", categoryByAlias);

			return "product/products_by_category";
		} catch (CategoryNotFoundException e) {
			return "error/404";
		}
	}
	
	@GetMapping("/product/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias,
							Model model) {
		try {
			Product product = productService.getProductByAlias(alias);
			List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
			
			model.addAttribute("pageTitle", product.getShortName());
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("product", product);
			
			return "product/product_detail";
		} catch (ProductNotFoundException e) {
			return "error/404";
		}
	}
	
	@GetMapping("/search")
	public String searchFirstPage(Model model,@Param("keyword") String keyword) {
		return searchByPage(model, keyword, 1);
	}
	
	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(Model model,@Param("keyword") String keyword,
					@PathVariable("pageNum") int pageNum) {
		
		Page<Product> pageProducts = productService.search(keyword, pageNum);
		List<Product> listResults = pageProducts.getContent();
		long startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;

		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProducts.getTotalElements());

		
		model.addAttribute("pageTitle", keyword + "- Search Result");
		model.addAttribute("keyword", keyword);
		model.addAttribute("listResults", listResults);
		
		return "product/search_result";
	}
}
