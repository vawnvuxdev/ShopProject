package com.evanshop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.evanshop.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CategoryRepositoryTest {
	
	@Autowired private CategoryRepository repo;
	
	@Test
	public void testListEnabledCategories() {
		List<Category> categories = repo.findAllEnabled();
		categories.forEach(c -> {
			System.out.println(c.getName() + ":" + c.isEnabled());
		});
	}
	
	@Test 
	public void testFindByAliasEnabled() {
		String alias = "electronics";
		
		Category category = repo.findByAliasEnabled(alias);
		
		assertThat(category).isNotNull();
	}
}
