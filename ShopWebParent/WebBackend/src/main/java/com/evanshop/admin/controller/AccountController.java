package com.evanshop.admin.controller;

import com.evanshop.admin.config.FileUploadUtil;
import com.evanshop.admin.config.sercurity.ShopUserDetails;
import com.evanshop.admin.service.UserService;
import com.evanshop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/account")
	public String viewAccountDetails(@AuthenticationPrincipal ShopUserDetails loggedUser, Model model) {
		
		String email = loggedUser.getUsername(); // user email
		User user = userService.getByEmail(email);
		
		model.addAttribute("user", user);
		
		return "users/account_form";
	}
	
	@PostMapping("/account/update")
	public String updateDetails(User user, RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal ShopUserDetails loggedUser,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			
			User savedUser = userService.updateDetails(user);
			
			String uploadDir = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
			userService.updateDetails(user);
		}
		
		loggedUser.setFirstName(user.getFirstName());
		loggedUser.setLastName(user.getLastName());
		
		redirectAttributes.addFlashAttribute("message", "The user information has been updated successfully!");

		return "redirect:/account";
	}
}
