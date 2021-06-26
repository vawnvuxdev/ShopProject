package com.evanshop.admin.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evanshop.admin.repository.CountryRepository;
import com.evanshop.admin.repository.CustomerRepository;
import com.evanshop.common.entity.Country;
import com.evanshop.common.entity.Customer;
import com.evanshop.common.exception.CustomerNotFoundException;

@Service
@Transactional
public class CustomerService {

	public static final int CUSTOMERS_PER_PAGE = 10;

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private CountryRepository countryRepo;
	@Autowired
	private PasswordEncoder encoder;

	public List<Customer> listAll() {
		return (List<Customer>) customerRepo.findAll();
	}

	public void enabledCustomer(Integer id, boolean enabled) {
		customerRepo.enable(id, enabled);
	}

	public Page<Customer> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending() ;
		
		Pageable pageable = PageRequest.of(pageNumber -1 , CUSTOMERS_PER_PAGE, sort);
		
		if(keyword != null) {
			return customerRepo.findAll(keyword, pageable);
		}
		
		return customerRepo.findAll(pageable);
	}
	
	public Customer getById(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CustomerNotFoundException("Could not find any customer with ID: " + id);
		}
	}
	
	
	public void save(Customer customerInForm) {
		if (!customerInForm.getPassword().isEmpty()) {
			String encodedPassword = encoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodedPassword);
		} else {
			Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();
			customerInForm.setPassword(customerInDB.getPassword());
		}
		customerRepo.save(customerInForm);
	}

	public void delete(Integer id) throws CustomerNotFoundException {
		Long count = customerRepo.countById(id);
		if(count == null || count == 0) {
			throw new CustomerNotFoundException("Could not find any user with ID: " + id);
		}
		customerRepo.deleteById(id);
	}
	
	public List<Country> listAllCountries(){
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		Customer existCustomer = customerRepo.findByEmail(email);
		
		if (existCustomer != null && existCustomer.getId() != id ) {
			return false;
		}
		
		return true;
	}
}
