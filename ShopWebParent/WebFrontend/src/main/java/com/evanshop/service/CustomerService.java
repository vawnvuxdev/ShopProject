package com.evanshop.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.evanshop.common.entity.Country;
import com.evanshop.common.entity.Customer;
import com.evanshop.repository.CountryRepository;
import com.evanshop.repository.CustomerRepository;

import net.bytebuddy.utility.RandomString;

@Service 
public class CustomerService {
	
	@Autowired private CustomerRepository customerRepo;
	@Autowired private CountryRepository countryRepo;
	@Autowired private PasswordEncoder passwordEncoder;

	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(String email) {
		Customer customerByEmail = customerRepo.findByEmail(email);
		return customerByEmail == null;
	}
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		
		customerRepo.save(customer);
	}
	
	public void encodePassword(Customer customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}
}
