package com.evanshop.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.evanshop.common.entity.Setting;
import com.evanshop.common.entity.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepoTests {
	
	@Autowired SettingRepository repo;
	
	@Test
	public void findByTwoCatgTest() {
		List<Setting> listByTwoCatg = repo.findByTwoCategory(SettingCategory.GENERAL, SettingCategory.CURRENCY);
		
		listByTwoCatg.forEach(System.out::println);
	}
}
