package com.evanshop.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.evanshop.admin.repository.BrandRepository;
import com.evanshop.common.entity.Brand;
import com.evanshop.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {
	
	@Autowired
	private BrandRepository repo;
	
	@Test
	public void testCreateBrand() {
		Category laptops = new Category(28);
		Brand acer = new Brand("Acer");
		acer.getCategories().add(laptops);
		
		Brand savedBrand = repo.save(acer);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateBrand2() {
		Category laptops = new Category(28);
		Category smartphones = new Category(49);
		Category tablets = new Category(50);
		
		Brand apple = new Brand("Apple");
		apple.getCategories().add(tablets);
		apple.getCategories().add(laptops);
		apple.getCategories().add(smartphones);
		
		Brand savedBrand = repo.save(apple);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateBrand3() {
		Category laptops = new Category(28);
		Category smartphones = new Category(49);
		Category tablets = new Category(50);
		Category memory  = new Category(31);
		Category display  = new Category(51);
		
		Brand samsung = new Brand("Samsung");
		samsung.getCategories().add(tablets);
		samsung.getCategories().add(laptops);
		samsung.getCategories().add(smartphones);
		samsung.getCategories().add(memory);
		samsung.getCategories().add(display);
		
		Brand savedBrand = repo.save(samsung);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testGetById() {
		Brand brand = repo.findById(1).get();
		
		assertThat(brand.getName()).isEqualTo("Acer");
	}

	@Test
	public void testUpdate() {
		String newName = "Samsung Electronics";
		Brand samsung = repo.findById(6).get();
		samsung.setName(newName);
		
		Brand updatedBrand = repo.save(samsung);
		assertThat(updatedBrand.getName()).isEqualTo(newName);
	}
	@Test
	public void testFindAll() {
		Iterable<Brand> brands = repo.findAll();
		
		brands.forEach(System.out::println);
		
		assertThat(brands).isNotEmpty();
	}
	
}
