package com.evanshop.admin.setting;

import java.util.List;

import com.evanshop.common.entity.Setting;
import com.evanshop.common.entity.SettingBag;

public class GeneralSettingBag extends SettingBag {

	public GeneralSettingBag(List<Setting> listSettings) {
		super(listSettings);
	}

	public void updateCurrencySymbol(String value) {
		super.udpate("CURRENCY_SYMBOL", value);
	}
	
	public void updateSiteLogo(String value) {
		super.udpate("SITE_LOGO", value);
	}
	
}
 