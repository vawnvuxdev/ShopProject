package com.evanshop.admin.controller;

import java.io.IOException;
import java.util.List;

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
import com.evanshop.admin.service.BrandService;
import com.evanshop.admin.service.CategoryService;
import com.evanshop.common.entity.Brand;
import com.evanshop.common.entity.Category;
import com.evanshop.common.exception.BrandNotFoundException;

@Controller
@RequestMapping("/brands")
public class BrandController {

	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService catgService;

//	@GetMapping("")
//	public String listBrands(Model model) {
//		List<Brand> listBrands = brandService.listAll();
//
//		model.addAttribute("listBrands", listBrands);
//
//		return "brands/brands";
//	}

	@GetMapping("")
	public String list1stPage(Model model) {
		return listByPage(model, 1, "name", "asc", null);
	}

	@GetMapping("/page/{pageNum}")
	public String listByPage(Model model, @PathVariable("pageNum") int pageNum, @Param("sortField") String sortField,
			@Param("sortDir") String sortDir, @Param("keyword") String keyword) {

		Page<Brand> page = brandService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Brand> listBrands = page.getContent();

		long startCount = (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
		long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;

		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("listBrands", listBrands);

		return "brands/brands";
	}

	@GetMapping("/new")
	public String createNewBrand(Model model) {

		List<Category> categoriesUseInForm = catgService.categoriesUseInForm();

		model.addAttribute("pageTitle", "Create new brand");
		model.addAttribute("listCategories", categoriesUseInForm);
		model.addAttribute("brand", new Brand());

		return "brands/brand_form";
	}

	@PostMapping("/save")
	public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);

			Brand savedBrand = brandService.save(brand);
			String uploadDir = "../brand-logos/" + savedBrand.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			System.out.println(brand.getCategories());
			brandService.save(brand);
		}

		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully!");
		return "redirect:/brands";
	}

	@GetMapping("/edit/{id}")
	public String editCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {

			Brand brand = brandService.findById(id);
			List<Category> categoriesUseInForm = catgService.categoriesUseInForm();

			model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");
			model.addAttribute("brand", brand);
			model.addAttribute("listCategories", categoriesUseInForm);

			return "brands/brand_form";
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/brands";
		}
	}

	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes)
			throws BrandNotFoundException {
		try {
			brandService.deleteById(id);
			String brandDir = "../brand-logos/" + id;
			FileUploadUtil.removeDir(brandDir);
		} catch (BrandNotFoundException ex) {
		}
		redirectAttributes.addFlashAttribute("message", "The brand with ID " + id + " has been deleted successfully!");
		return "redirect:/brands";
	}

}
