package com.evanshop.admin.category;

import com.evanshop.admin.repository.CategoryRepository;
import com.evanshop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {
	
	@Autowired
	private CategoryRepository repo;
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		Category savedCategory = repo.save(category);
		
		assertThat(savedCategory.getId()).isGreaterThan(0);	
	}
	
	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(3);
		Category subCategory1 = new Category("Gaming Laptop", parent);
		Category subCategory2 = new Category("Business Laptop", parent);

		repo.saveAll(List.of(subCategory1, subCategory2));
	}
	
	@Test
	public void testGetCategory() {
		Category category = repo.findById(2).get();
		System.out.println(category.getName());

		Set<Category> children = category.getChildren();

		for (Category subCategory : children) {
			System.out.println(subCategory.getName());
		}
		
		assertThat(children.size()).isGreaterThan(0);
	}
	
	@Test
	public void testPrintHierarchicalCategories() {
		Iterable<Category> categories = repo.findAll();
		
		for(Category category : categories) {
			if (category.getParent() == null) {
				System.out.println("." + category.getName());
				
				Set<Category> childrens = category.getChildren();
				
				for(Category subCategory : childrens) {
					System.out.println("|_." + subCategory.getName());
					printChildren(subCategory, 1);
				}
			}
		}
	}
	
	public void printChildren(Category parent, int subLevel) {
		int newSubLevel = subLevel + 1 ;
		Set<Category> children = parent.getChildren();
		
		for(Category subCategory : children) {
			for (int i = 0; i < newSubLevel; i++) {
				System.out.print(" ");
			}
			System.out.println("|_." + subCategory.getName());
			printChildren(subCategory, newSubLevel);
		}
	}
	
	@Test
	public void testListRootCategories() {
		List<Category> listRootCategories = repo.findRootCategories(Sort.by("name").ascending());
		listRootCategories.forEach(c -> System.out.println(c.getName()));
	}
	
	@Test
	public void testFindByName() {
		String name = "Computers";
		Category category = repo.findByName(name);
		
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}

	@Test
	public void testFindByAlias() {
		String alias = "Electronics";
		Category category = repo.findByName(alias);
		
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(alias);
	}
}
