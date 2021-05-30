package com.evanshop.admin.restController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evanshop.admin.repository.CountryRepository;
import com.evanshop.common.entity.Country;

@RestController
public class CountryRestController {
	
	@Autowired private CountryRepository repository;
	
	@GetMapping("/countries/list")
	public List<Country> listAll(){
		return repository.findAllByOrderByNameAsc();
	}
	
	@PostMapping("/countries/save")
	public String save(Country country) {
		Country savedCountry = repository.save(country);
		return String.valueOf(savedCountry.getId());
	}
}
