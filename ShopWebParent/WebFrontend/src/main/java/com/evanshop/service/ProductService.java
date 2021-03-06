package com.evanshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.evanshop.common.entity.Product;
import com.evanshop.common.exception.ProductNotFoundException;
import com.evanshop.repository.ProductRepository;

@Service 
public class ProductService {

	public static final int PRODUCTS_PER_PAGE = 10;
	public static final int SEARCH_RESULTS_PER_PAGE = 10;
	
	@Autowired private ProductRepository repo;

	public Page<Product> listProductsByCategory(int pageNum, Integer categoryId){
		String categoryIdMatch = "-" + categoryId + "-"; 
		Pageable pageable = PageRequest.of(pageNum -1, PRODUCTS_PER_PAGE);
		
		Page<Product> listProductsByCategory = repo.listByCategory(categoryId, categoryIdMatch, pageable);
		
		return listProductsByCategory;
	}
	
	public Product getProductByAlias(String alias) throws ProductNotFoundException {
		Product product = repo.findByAlias(alias);
		
		if(product == null) {
			throw new ProductNotFoundException("Could not found Product !");
		}
		
		return product;
	}
	
	public Page<Product> search(String keyword, int pageNum){
		Pageable pageable = PageRequest.of(pageNum -1, SEARCH_RESULTS_PER_PAGE);
		return repo.search(keyword, pageable);
	}
}
