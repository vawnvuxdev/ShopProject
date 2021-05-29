package com.evanshop.admin.country;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.evanshop.admin.repository.CountryRepository;
import com.evanshop.common.entity.Country;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepoTests {
	
	@Autowired CountryRepository repo;
	
	@Test
	public void testCreateCountry() {
		Country country = new Country("China", "CN");
		
		Country savedCountry = repo.save(country);
		
		assertThat(savedCountry).isNotNull();
	}
	
	@Test
	public void testDeleteCountry() {
		Country china = repo.findById(1).get();
		
		repo.delete(china);
	}
	
	@Test
	public void testUpdateCountry() {
		Country china = repo.findById(2).get();
		china.setName("Tau Khua");
		Country savedCountry = repo.save(china);
		
		assertThat(savedCountry).isNotNull();
	}
	
	@Test
	public void testCreateListCountry() {
		List<Country> countries = new ArrayList<>();
		countries.add(new Country("Republic of India", "IN"));
		countries.add(new Country("United State", "US"));
		countries.add(new Country("United Kingdom", "UK"));
		countries.add(new Country("Viet Nam", "VN"));
		countries.add(new Country("Japan", "JP"));
		
		repo.saveAll(countries);
		
		assertThat(countries.size()).isGreaterThan(0);
	}
}
