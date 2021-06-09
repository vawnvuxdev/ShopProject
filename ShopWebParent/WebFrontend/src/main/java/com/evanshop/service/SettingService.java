package com.evanshop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evanshop.common.entity.Setting;
import com.evanshop.common.entity.SettingCategory;
import com.evanshop.repository.SettingRepository;
import com.evanshop.setting.EmailSettingBag;

@Service
public class SettingService {

	@Autowired
	private SettingRepository repository;
 
	public List<Setting> getGeneralSettings() {
		return repository.findByTwoCategory(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}
	
	public EmailSettingBag getEmailSettings() {
		List<Setting> settings = repository.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(repository.findByCategory(SettingCategory.MAIL_TEMPLATE));
		
		return new EmailSettingBag(settings);
	}
	
}
