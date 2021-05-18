package com.evanshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.evanshop.common.entity.Product;
import com.evanshop.repository.ProductRepository;

@Service 
public class ProductService {

	public static final int PRODUCTS_PER_PAGE = 10;
	
	@Autowired private ProductRepository repo;

	public Page<Product> listProductsByCategory(int pageNumber, Integer categoryId){
		String categoryIdMatch = "-" + categoryId + "-"; 
		Pageable pageable = PageRequest.of(pageNumber -1, PRODUCTS_PER_PAGE);
		
		Page<Product> listProductsByCategory = repo.listByCategory(categoryId, categoryIdMatch, pageable);
		
		return listProductsByCategory;
	}
}
