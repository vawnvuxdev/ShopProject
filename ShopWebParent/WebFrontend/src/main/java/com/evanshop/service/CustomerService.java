package com.evanshop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evanshop.common.entity.Country;
import com.evanshop.repository.CountryRepository;
import com.evanshop.repository.CustomerRepository;

@Service 
public class CustomerService {
	
	@Autowired CustomerRepository customerRepo;
	@Autowired CountryRepository countryRepo;

	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	
}
