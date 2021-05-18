package com.evanshop.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.evanshop.admin.repository.ProductRepository;
import com.evanshop.common.entity.Brand;
import com.evanshop.common.entity.Category;
import com.evanshop.common.entity.Product;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepoTest {

	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 37);
		Category category = entityManager.find(Category.class, 5);
		
		Product product = new Product();
		
		product.setName("Acer Aspire Desktop");
		product.setAlias("acer_aspire_desktop");
		product.setShortDescription("Short description for Acer Aspire Desktop !");
		product.setFullDescription("Full description for Acer Aspire Desktop !");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		product.setPrice(678);
		product.setCost(600);
		
		product.setEnabled(true);
		product.setInStock(true);
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAll() {
		Iterable<Product> listAll = repo.findAll();
		
		listAll.forEach(System.out::println);
	}
	
	@Test
	public void testGetById() {
		Integer id = 2;
		Product productById = repo.findById(id).get();
		System.out.println(productById);
		
		assertThat(productById).isNotNull();
	}
	
	@Test
	public void testUpdateProduct() {
		Integer id = 1;
		Product productById = repo.findById(id).get();
		
		productById.setShortDescription("Short description for Samsung Galaxy A31 !");
		productById.setFullDescription("Full description for Samsung Galaxy A31 !");
		productById.setPrice(499);
		
		repo.save(productById);
		
		Product updatedProduct = entityManager.find(Product.class, id);
		
		assertThat(updatedProduct.getPrice()).isEqualTo(499);
	}
	
	@Test
	public void testDeletePro() {
		Integer id = 3;
		repo.deleteById(id);
		
		Optional<Product> deletedProduct = repo.findById(id);
		
		assertThat(deletedProduct.isPresent());
	}
	
	@Test
	public void testSaveWithImage() {
		Integer id = 1;
		Product productById = repo.findById(id).get();
		
		productById.setMainImage("main-image.jpg");
		productById.addExtraImage("extra-iamge-1.jpg");
		productById.addExtraImage("extra-iamge-2.jpg");
		productById.addExtraImage("extra-iamge-3.jpg");
		
		Product savedProduct = repo.save(productById);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
	}
	
	@Test
	public void testSaveWithDetails() {
		Integer id = 1;
		Product productById = repo.findById(id).get();
		
		productById.addDetail("OS","Android 10");
		productById.addDetail("RAM","4GB");
	
		Product savedProduct = repo.save(productById);
		
		assertThat(savedProduct.getDetails()).isNotEmpty();
	}
}
