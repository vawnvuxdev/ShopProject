package com.evanshop.admin.user;

import com.evanshop.admin.repository.RoleRepository;
import com.evanshop.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Test
	public void testCreateFirstRole() {
		Role roleAdmin = new Role("Admin","Manage everything");
		Role savedRole = roleRepo.save(roleAdmin);
		
		assertThat(savedRole.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateRestRole() {
		Role roleSaleman = new Role("Sale man","Manage everything about product sale.");
		Role roleEditor = new Role("Editor","Manage categories, bands, products, articles and menus.");
		Role roleShipper = new Role("Shipper","View products, orders and update status.");
		Role roleAssitant = new Role("Assistant","Manage questions and reviews.");
		
		roleRepo.saveAll(List.of(roleAssitant, roleEditor, roleSaleman, roleShipper));
	}
	
	@Test
	public void updateRole() {
		Role roleAdmin = roleRepo.findById(1).get();
		roleAdmin.setDescription("Manage everything");
		roleRepo.save(roleAdmin);
	}
}
