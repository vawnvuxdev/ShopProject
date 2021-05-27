package com.evanshop.admin.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.evanshop.admin.config.FileUploadUtil;
import com.evanshop.admin.service.UserService;
import com.evanshop.admin.service.exporter.UserCsvExporter;
import com.evanshop.admin.service.exporter.UserExcelExporter;
import com.evanshop.admin.service.exporter.UserPdfExporter;
import com.evanshop.common.entity.Role;
import com.evanshop.common.entity.User;
import com.evanshop.common.exception.UserNotFoundException;

@Controller
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

//	@GetMapping("/users")
//	public String listAll(Model model) {
//		List<User> listUsers = userService.listAll();
//		model.addAttribute("listUsers", listUsers);
//		return "users";
//	}

	@GetMapping("")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "firstName", "asc", null);
	}

	@GetMapping("/page/{pageNumber}")
	public String listByPage(@PathVariable("pageNumber") int pageNumber, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {

		Page<User> page = userService.listByPage(pageNumber, sortField, sortDir, keyword);
		List<User> listUsers = page.getContent();

		long startCount = (pageNumber - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;

		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("listUsers", listUsers);

		return "users/users";
	}

	@GetMapping("/new")
	public String createNew(Model model) {
		List<Role> listRoles = userService.roleList();
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		return "users/user_form";
	}

	@PostMapping("/save")
	public String save(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = userService.save(user);
			String uploadDir = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
			userService.save(user);
		}
		redirectAttributes.addFlashAttribute("message", "The user information has been saved successfully!");

		return getRedirectURLtoEffectedUser(user);
	}

	private String getRedirectURLtoEffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
	}

	@GetMapping("/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
			User user = userService.getById(id);
			List<Role> listRoles = userService.roleList();
			model.addAttribute("listRoles", listRoles);
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}
	}

	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			userService.deleteById(id);
			String deleteDir = "user-photos/" + id;
			FileUploadUtil.removeDir(deleteDir);
			redirectAttributes.addFlashAttribute("message",
					"The User with ID " + id + " has been deleted successfully!");
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/users";
	}

	@GetMapping("/{id}/enabled/{status}")
	public String updateStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {

		userService.updateUserEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The user id " + id + " has been " + status + "!";

		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/users";
	}

	@GetMapping("/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers = userService.listAll();
		UserCsvExporter csvExporter = new UserCsvExporter();
		csvExporter.export(listUsers, response);
	}

	@GetMapping("/export/exel")
	public void exportToExel(HttpServletResponse response) throws IOException {
		List<User> listUsers = userService.listAll();
		UserExcelExporter excelExporter = new UserExcelExporter();
		excelExporter.export(listUsers, response);
	}

	@GetMapping("/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {
		List<User> listUsers = userService.listAll();
		UserPdfExporter pdfExporter = new UserPdfExporter();
		pdfExporter.export(listUsers, response);
	}

}
