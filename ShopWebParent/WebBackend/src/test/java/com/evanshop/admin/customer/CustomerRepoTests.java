package com.evanshop.admin.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.evanshop.admin.repository.CustomerRepository;
import com.evanshop.common.entity.Country;
import com.evanshop.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepoTests {

	@Autowired CustomerRepository repository;
	@Autowired TestEntityManager entityManager;
	
	@Test
	public void testCreate() {
		Integer usaId = 234;
		Country usa = entityManager.find(Country.class, usaId);
		
		Customer customer = new Customer();
		customer.setFirstName("Barac");
		customer.setLastName("Obama");
		customer.setCountry(usa);
		customer.setCity("New York");
		customer.setState("Washington");
		customer.setPostalCode("123412");
		customer.setEmail("obama@gmail.com");
		customer.setPassword("obamapassword");
		customer.setCreatedTime(new Date());
		customer.setPhoneNumber("12341231234");
		customer.setAddressLine1("213 New York");
	
		Customer savedCustomer = repository.save(customer);
	
		assertThat(savedCustomer).isNotNull();
	}
}
