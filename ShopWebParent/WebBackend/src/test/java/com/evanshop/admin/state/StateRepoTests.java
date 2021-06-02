package com.evanshop.admin.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.evanshop.admin.repository.StateRepository;
import com.evanshop.common.entity.Country;
import com.evanshop.common.entity.State;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepoTests {
	
	@Autowired StateRepository repo;
	@Autowired TestEntityManager entityManager;
	
	@Test
	public void testCreateState() {
		Country vn = entityManager.find(Country.class, 4);
		Country us = entityManager.find(Country.class, 2);
		Country uk = entityManager.find(Country.class, 3);
		
		State state = repo.save(new State("London", uk));
		
		assertThat(state).isNotNull();
	}
	
	@Test
	public void testGetState() {
		Integer stateId = 2;
		Optional<State> findById = repo.findById(stateId);
		assertThat(findById.isPresent()); 
	}
	
	@Test
	public void testDeleteState() {
		repo.deleteById(3);
		Optional<State> findById = repo.findById(3);
		assertThat(findById.isEmpty());
	}
}
