package com.evanshop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evanshop.common.entity.Setting;
import com.evanshop.common.entity.SettingCategory;
import com.evanshop.repository.SettingRepository;

@Service
public class SettingService {

	@Autowired
	private SettingRepository repository;
 
	public List<Setting> getGeneralSettings() {
		List<Setting> settings = new ArrayList<>();

		List<Setting> generalSettings = repository.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySettings = repository.findByCategory(SettingCategory.CURRENCY);
		
		settings.addAll(generalSettings);
		settings.addAll(currencySettings);
		return settings;
	}
	

}
