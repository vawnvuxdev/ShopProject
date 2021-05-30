package com.evanshop.admin.restController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evanshop.admin.dto.CategoryDTO;
import com.evanshop.admin.exception.BrandNotFoundRestException;
import com.evanshop.admin.service.BrandService;
import com.evanshop.common.entity.Brand;
import com.evanshop.common.entity.Category;
import com.evanshop.common.exception.BrandNotFoundException;

@RestController
public class BrandRestController {
	
	@Autowired
	private BrandService brandService;

	@PostMapping("/brands/check_unique")
	public String checkUnique(
			@Param("id") Integer id,@Param("name") String name) {
		return brandService.checkUnique(id, name);
	}
	
	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> listCategoriesByBrand(@PathVariable("id") Integer brandId) throws BrandNotFoundRestException{
		List<CategoryDTO> listCategories = new ArrayList<>() ;
		
		try {
			Brand brand = brandService.findById(brandId);
			Set<Category> categories = brand.getCategories();
			
			for(Category c : categories) {
				CategoryDTO dto = new CategoryDTO(c.getId(), c.getName());
				listCategories.add(dto);
			}
			
			return listCategories; 
		} catch (BrandNotFoundException e) {
			throw new BrandNotFoundRestException();
		}
		
		
	}
}
