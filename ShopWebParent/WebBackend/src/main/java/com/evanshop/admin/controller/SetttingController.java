package com.evanshop.admin.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.evanshop.admin.config.FileUploadUtil;
import com.evanshop.admin.repository.CurrencyRepository;
import com.evanshop.admin.service.SettingService;
import com.evanshop.admin.setting.GeneralSettingBag;
import com.evanshop.common.entity.Currency;
import com.evanshop.common.entity.Setting;

@Controller
@RequestMapping("/settings")
public class SetttingController {

	@Autowired
	private SettingService settingService;
	@Autowired
	private CurrencyRepository currencyRepository;

	@GetMapping("")
	public String listAll(Model model) {

		List<Setting> listSettings = settingService.listAllSettings();
		List<Currency> listCurrencies = currencyRepository.findAllOrderByNameAsc();

		model.addAttribute("listCurrencies", listCurrencies);

		for (Setting setting : listSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}

		return "/settings/settings";
	}

	@PostMapping("/save_general")
	public String saveGeneralSettings(Model model, @RequestParam("fileImage") MultipartFile multipartFile,
			HttpServletRequest request, RedirectAttributes ra) throws IOException {
		GeneralSettingBag settingBag = settingService.getGeneralSettings();
		List<Setting> settings = settingBag.list();
		
		
		saveSiteLogo(multipartFile, settingBag);
		saveCurrencySymbol(request, settingBag);
		updateSettingValue(request, settings);
		
		settings.forEach(System.out::println);
		
		settingService.saveAll(settings);
		
		ra.addFlashAttribute("message", "General settings have been saved !");

		return "redirect:/settings";
	}

	private void updateSettingValue(HttpServletRequest request, List<Setting> settings) {
		for(Setting setting : settings) {
			String value = request.getParameter(setting.getKey());
			if (value != null) {
				setting.setValue(value);
			}
		}
	}

	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency> findByIdResult = currencyRepository.findById(currencyId);
		
		if(findByIdResult.isPresent()) {
			Currency currency = findByIdResult.get();
			String value = currency.getSymbol();
			settingBag.updateCurrencySymbol(value);
		}
	}

	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			settingBag.updateSiteLogo(value);
			String uploadDir = "../site-logo/";
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
	}

	@PostMapping("/save_mail_server")
	public String saveMailServerSettigng(Model model, HttpServletRequest request, RedirectAttributes ra) {
		List<Setting> mailServerSetting = settingService.getMailServerSetting();
		
		updateSettingValue(request, mailServerSetting);
		settingService.saveAll(mailServerSetting);
		
		ra.addFlashAttribute("message", "Mail Server settings have been saved !");

		return "redirect:/settings";
	}
	
	@PostMapping("/save_mail_templates")
	public String saveMailTemplatesSettigng(Model model, HttpServletRequest request, RedirectAttributes ra) {
		List<Setting> mailTemplatesSetting = settingService.getMailTemplatesSetting();
		
		updateSettingValue(request, mailTemplatesSetting);
		settingService.saveAll(mailTemplatesSetting);
		
		ra.addFlashAttribute("message", "Mail Templates settings have been saved !");
		
		return "redirect:/settings";
	}
	
}
