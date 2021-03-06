package com.evanshop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.evanshop.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProducRepositortyTest {

	@Autowired ProductRepository repo;

	@Test 
	public void testFindByAlias() {
		String alias = "Acer-Aspire-5-Slim-Laptop";
		
		Product product = repo.findByAlias(alias);
	
		assertThat(product).isNotNull();
	}
}
