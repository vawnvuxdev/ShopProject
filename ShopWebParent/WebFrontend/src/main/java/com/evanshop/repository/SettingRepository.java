package com.evanshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.evanshop.common.entity.Setting;
import com.evanshop.common.entity.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String> {
	
	public List<Setting> findByCategory(SettingCategory setting);
	
	@Query("SELECT s FROM Setting s WHERE s.category =?1 OR s.category =?2")
	public List<Setting> findByTwoCategory(SettingCategory catg1st, SettingCategory catg2nd);
	
}
