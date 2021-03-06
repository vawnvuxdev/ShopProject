package com.evanshop.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evanshop.admin.repository.SettingRepository;
import com.evanshop.admin.setting.GeneralSettingBag;
import com.evanshop.common.entity.Setting;
import com.evanshop.common.entity.SettingCategory;

@Service
public class SettingService {

	@Autowired
	private SettingRepository repository;

	public List<Setting> listAllSettings() {
		return (List<Setting>) repository.findAll();
	}

	public GeneralSettingBag getGeneralSettings() {
		List<Setting> generalSettings = repository.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySettings = repository.findByCategory(SettingCategory.CURRENCY);
		List<Setting> settings = new ArrayList<>();
		
		settings.addAll(generalSettings);
		settings.addAll(currencySettings);
		return new GeneralSettingBag(settings);
	}
	
	public void saveAll(Iterable<Setting> settings) {
		repository.saveAll(settings);
	}
	
	public List<Setting> getMailServerSetting(){
		return repository.findByCategory(SettingCategory.MAIL_SERVER);
	}
	
	public List<Setting> getMailTemplatesSetting(){
		return repository.findByCategory(SettingCategory.MAIL_TEMPLATE);
	}

}
