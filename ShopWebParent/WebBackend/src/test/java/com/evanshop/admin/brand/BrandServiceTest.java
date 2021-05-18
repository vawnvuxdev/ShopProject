package com.evanshop.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.evanshop.admin.repository.BrandRepository;
import com.evanshop.admin.service.BrandService;
import com.evanshop.common.entity.Brand;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BrandServiceTest {
	
	@MockBean
	private BrandRepository repo;
	
	@InjectMocks
	private BrandService service;
	
	@Test
	public void testCheckUniqueCreateDuplicate() {
		Integer id = 2;
		String name = "Apple";
		Brand brand = new Brand(name);

		Mockito.when(repo.findByName(name)).thenReturn(brand);
		
		String result = service.checkUnique(id, name);
		
		assertThat(result).isEqualTo("Duplicate");
	}
	
	@Test
	public void testCheckUniqueCreateOk() {
		Integer id = null;
		String name = "Test";

		Mockito.when(repo.findByName(name)).thenReturn(null);
		
		String result = service.checkUnique(id, name);
		
		assertThat(result).isEqualTo("OK");
	}
	@Test
	public void testCheckUniqueEditDuplicate() {
		Integer id = 2;
		String name = "Apple";
		Brand brand = new Brand(id, name);
		
		Mockito.when(repo.findByName(name)).thenReturn(brand);
		
		String result = service.checkUnique(3, "Apple");
		
		assertThat(result).isEqualTo("Duplicate");
	}
	
	@Test
	public void testCheckUniqueEditOk() {
		Integer id = 2;
		String name = "Apple";
		Brand brand = new Brand(id, name);
		
		Mockito.when(repo.findByName(name)).thenReturn(brand);
		
		String result = service.checkUnique(id, name);
		
		assertThat(result).isEqualTo("OK");
	}
	
}
