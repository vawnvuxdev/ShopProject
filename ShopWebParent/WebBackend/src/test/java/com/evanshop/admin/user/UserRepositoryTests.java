package com.evanshop.admin.user;

import com.evanshop.admin.repository.UserRepository;
import com.evanshop.common.entity.Role;
import com.evanshop.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateUserWithOneRow() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userTest = new User("admin@gmail.com", "adminpassword", "Admin", "User");
		userTest.addRole(roleAdmin);

		User savedUser = userRepo.save(userTest);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateUserWithMoreRoles() {

		User userEvan = new User("evan@gmail.com", "evanpassword", "Micheal", "Evan");
		Role editor = new Role(4);
		Role assitant = new Role(2);
		userEvan.addRole(editor);
		userEvan.addRole(assitant);

		User saveUserEvan = userRepo.save(userEvan);
		assertThat(saveUserEvan.getId()).isGreaterThan(0);
	}

	@Test
	public void testListAllUser() {
		Iterable<User> userList = userRepo.findAll();
		userList.forEach(user -> System.out.println("user"));
	}

	@Test
	public void testGetUserById() {
		User test = userRepo.findById(1).get();
		System.out.println(test);
		assertThat(test).isNotNull();
	}

	@Test
	public void testUpdateUserDetails() {
		Role adminRole = new Role(1);
		User test = userRepo.findById(1).get();
		test.setEmail("admin@gmail.com");
		test.setEnabled(true);
		test.addRole(adminRole);

		userRepo.save(test);
	}

	@Test
	public void testUpdateUserRole() {
		User test = userRepo.findById(2).get();
		Role adminRole = new Role(1);
		test.getRoles().remove(adminRole);
		userRepo.save(test);
	}

	@Test
	public void deleteUser() {
		Integer userId = 4;
		userRepo.deleteById(userId);
	}

	@Test
	public void deleteAll() {
		Iterable<User> userList = userRepo.findAll();
		userList.forEach(user -> userRepo.deleteById(user.getId()));
	}

	@Test
	public void testGetUserByEmail() {
		String email = "eva123n@gmail.com";
		User user = userRepo.getUserByEmail(email);

		assertThat(user).isNotNull();
	}

	@Test
	public void testCountById() {
		Integer id = 1;
		long countByIt = userRepo.countById(id);

		assertThat(countByIt).isNotNull().isGreaterThan(0);
	}

	@Test
	public void testDisableUser() {
		Integer id = 1;
		userRepo.updateEnabledStatus(id, false);
	}

	@Test
	public void testEnableUser() {
		Integer id = 1;
		userRepo.updateEnabledStatus(id, true);
	}

	@Test
	public void testListFirstPage() {
		int pageNumber = 1;
		int pageSize = 4;

//		Pageable from org.springframework.data.domain
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = userRepo.findAll(pageable);

		List<User> listUsers = page.getContent();
		listUsers.forEach(user -> System.out.println(user));

		assertThat(listUsers.size()).isEqualTo(pageSize);
	}

	@Test
	public void testSearchUsers() {
		String keyword = "evan";

		int pageNumber = 0;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = userRepo.findAll(keyword ,pageable);
		
		List<User> listUsers = page.getContent();
		listUsers.forEach(user -> System.out.println(user));
		assertThat(listUsers.size()).isGreaterThan(0);
	}

}
