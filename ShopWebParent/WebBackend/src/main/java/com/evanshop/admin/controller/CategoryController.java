package com.evanshop.admin.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.evanshop.admin.exception.CategoryNotFoundException;
import com.evanshop.admin.service.CategoryPageInfo;
import com.evanshop.admin.service.CategoryService;
import com.evanshop.common.entity.Category;

@Controller
@RequestMapping("/categories")
public class CategoryController {

	@Autowired
	private CategoryService catgService;

//	@GetMapping("")
//	public String listCategories(Model model) {
//		List<Category> listCategories = catgService.listAll();
//		model.addAttribute("listCategories", listCategories);
//		return "categories/categories";
//	}

	// Update sort
	@GetMapping("")
	public String listFirstPage(Model model, @Param("sortDir") String sortDir) {

//		if (sortDir == null || sortDir.isEmpty()) {
//			sortDir = "asc";
//		}
//
//		List<Category> listCategories = catgService.listAll(sortDir);
//
//		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
//
//		model.addAttribute("listCategories", listCategories);
//		model.addAttribute("reverseSortDir", reverseSortDir);

		return listByPage(1, model, sortDir, null);
	}

	@GetMapping("/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, 
			Model model, @Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		if (sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> listCategories = catgService.listByPage(pageInfo, pageNum ,sortDir, keyword);

		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;

		if (endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("listCategories", listCategories);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());

		return "categories/categories";
	}

	@GetMapping("/new")
	public String createNewCategory(Model model) {
		List<Category> listCategories = catgService.categoriesUseInForm();

		model.addAttribute("listCategories", listCategories);
		model.addAttribute("category", new Category());
		model.addAttribute("pageTitle", "Create new category");
		return "categories/category_form";
	}

	@PostMapping("/save")
	public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);

			Category savedCategory = catgService.save(category);
			String uploadDir = "../category-images/" + savedCategory.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			catgService.save(category);
		}

		redirectAttributes.addFlashAttribute("message", "The category has been created successfully!");
		return "redirect:/categories";
	}

	@GetMapping("/edit/{id}")
	public String editCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
			Category category = catgService.findById(id);
			List<Category> listCategories = catgService.categoriesUseInForm();
//			Category parent = category.getParent();
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("category", category);
			model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");
			return "categories/category_form";
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/categories";
		}
	}

	@GetMapping("/{id}/enabled/{status}")
	public String updateStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {
		catgService.updateCatgEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The category id " + id + " has been " + status + "!";
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/categories";
	}

	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes)
			throws CategoryNotFoundException {
		Category delCatg = catgService.findById(id);
		Set<Category> children = delCatg.getChildren();

		try {
			for (Category subCatg : children) {
				try {
					catgService.deleteById(subCatg.getId());
					String subCatgDir = "../category-images/" + subCatg.getId();
					FileUploadUtil.removeDir(subCatgDir);
				} catch (CategoryNotFoundException e) {
				}
			}
			catgService.deleteById(id);
			String catgDir = "../category-images/" + id;
			FileUploadUtil.removeDir(catgDir);
			redirectAttributes.addFlashAttribute("message",
					"The Category with ID " + id + " has been deleted successfully!");
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/categories";
	}
}
