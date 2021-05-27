package com.evanshop.admin.service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evanshop.admin.repository.ProductRepository;
import com.evanshop.common.entity.Product;
import com.evanshop.common.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {
	
	public static final int PRODUCTS_PER_PAGE = 5;

	@Autowired
	private ProductRepository repo;

	public List<Product> listAll() {
		return (List<Product>) repo.findAll();
	}

	public Page<Product> listByPage(int pageNumber, String sortField, String sortDir, String keyword, Integer categoryId) {
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending() ;
		
		Pageable pageable = PageRequest.of(pageNumber -1 , PRODUCTS_PER_PAGE, sort);
		
		if (keyword != null && !keyword.isEmpty()) {
			if (categoryId != null && categoryId > 0 ) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				return repo.searchInCategory(categoryId, categoryIdMatch,keyword , pageable);
			}
			return repo.findAll(keyword, pageable);
		}
		
		if (categoryId != null && categoryId > 0 ) {
			String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
			return repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
		}
		
		return repo.findAll(pageable);
	}
	
	public Product save(Product product) {
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}

		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replace(" ", "-");
			product.setAlias(defaultAlias);
		} else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}

		product.setUpdatedTime(new Date());

		return repo.save(product);
	}
	
	public void saveProductPrice(Product productInForm) {
		Product productInDB = repo.findById(productInForm.getId()).get();
		productInDB.setCost(productInForm.getCost());
		productInDB.setPrice(productInForm.getPrice());
		productInDB.setDiscountPercent(productInForm.getDiscountPercent());
		
		repo.save(productInDB);

	}
	

	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Product productByName = repo.findByName(name);

		if (isCreatingNew) {
			if (productByName != null)
				return "Duplicate";
		} else {
			if (productByName != null && productByName.getId() != id) {
				return "Duplicate";
			}
		}
		return "OK";
	}

	public Product findById(Integer id) throws ProductNotFoundException {
		try {
			Product product = repo.findById(id).get();
			return product;
		} catch (NoSuchElementException e) {
			throw new ProductNotFoundException("Could not find Product with id: " + id);
		}
	}

	public void updateProductEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}

	public void deleteById(Integer id) throws ProductNotFoundException {
		Long countById = repo.countById(id);
		if (countById == null || countById == 0) {
			throw new ProductNotFoundException("Cound not found product with id: " + id);
		}
		repo.deleteById(id);
	}
}
