package com.evanshop.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.evanshop.common.entity.Country;
import com.evanshop.common.entity.Customer;
import com.evanshop.config.Utility;
import com.evanshop.service.CustomerService;
import com.evanshop.service.SettingService;
import com.evanshop.setting.EmailSettingBag;

@Controller
public class CustomerController {

	@Autowired private CustomerService  service;
	@Autowired private SettingService  settingService;
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		List<Country> listCountries = service.listAllCountries();
		
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Customer Register");
		model.addAttribute("customer", new Customer());
		
		return "register/register_form";
	}
	
	@PostMapping("/create_customer")
	public String createCustomer(Model model, Customer customer,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
		service.registerCustomer(customer);
		sendVerificationMail(request, customer);
		
		model.addAttribute("pageTitle", "Registration Succeeded!");
		
		return "register/register_success";
	}

	private void sendVerificationMail(HttpServletRequest request, Customer customer) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = customer.getEmail();
		String subject = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		content = content.replace("[[name]]", customer.getFullName());
		
		String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode() ;

		content = content.replace("[[URL]]", verifyURL);
		
		helper.setFrom(emailSettings.getUsername(), emailSettings.getMailSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		helper.setText(content, true);
		
		mailSender.send(message);
		
		System.out.println(toAddress);
		System.out.println(verifyURL);
		
	}
}
