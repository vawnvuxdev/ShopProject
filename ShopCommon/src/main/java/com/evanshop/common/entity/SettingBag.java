package com.evanshop.common.entity;

import java.util.List;

public class SettingBag {

	private List<Setting> listSettings;

	public SettingBag() {
	}

	public SettingBag(List<Setting> listSettings) {
		this.listSettings = listSettings;
	}

	public List<Setting> getListSettings() {
		return listSettings;
	}

	public void setListSettings(List<Setting> listSettings) {
		this.listSettings = listSettings;
	}
	
	public Setting get(String key) {
		int index = listSettings.indexOf(new Setting(key));
		
		if(index > 0) {
			return listSettings.get(index);
		}
		
		return null;
	}

	public String getValue(String key) {
		Setting setting = get(key);
		if(setting != null) {
			return setting.getValue();
		}
		
		return null;
	}
	
	public void udpate (String key, String value) {
		Setting setting = get(key);
		
		if(setting != null && value != null) {
			setting.setKey(key);
			setting.setValue(value);
		}
		
	}
	
	public List<Setting> list(){
		return listSettings;
	}
}
