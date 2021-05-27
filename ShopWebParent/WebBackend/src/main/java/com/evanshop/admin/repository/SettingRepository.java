package com.evanshop.admin.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.evanshop.common.entity.Setting;
import com.evanshop.common.entity.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String> {
	
	public List<Setting> findByCategory(SettingCategory setting);
}
