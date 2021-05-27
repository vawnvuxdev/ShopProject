package com.evanshop.admin.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.evanshop.admin.config.FileUploadUtil;
import com.evanshop.admin.config.sercurity.ShopUserDetails;
import com.evanshop.admin.service.BrandService;
import com.evanshop.admin.service.CategoryService;
import com.evanshop.admin.service.ProductService;
import com.evanshop.common.entity.Category;
import com.evanshop.common.entity.Product;
import com.evanshop.common.exception.ProductNotFoundException;

@Controller
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;

//	@GetMapping("")
//	public String listAll(Model model) {
//
//		model.addAttribute("listProducts", productService.listAll());
//
//		return "products/products";
//	}

	@GetMapping("")
	public String list1stPage(Model model) {
		return listByPage(model, 1, "name", "asc", null, 0);
	}

	@GetMapping("/page/{pageNum}")
	public String listByPage(Model model, @PathVariable("pageNum") int pageNum, 
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir, 
			@Param("keyword") String keyword,
			@Param("categoryId") Integer categoryId) {
		
		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
		List<Product> listProducts = page.getContent();
		
		List<Category> listCategories = categoryService.categoriesUseInForm();

		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;

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
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("listCategories", listCategories);
		
		if(categoryId != null ) model.addAttribute("categoryId", categoryId);

		return "products/products";
	}
	
	
	
	///////////////////////////////////////////////////////////////
	@GetMapping("/new")
	public String newProduct(Model model) {

		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);

		model.addAttribute("pageTitle", "Create new Product");
		model.addAttribute("numberOfExistingExtraImages", product.getImages().size());
		model.addAttribute("listBrands", brandService.listAll());
		model.addAttribute("product", product);

		return "products/product_form";
	}

	///////////////////////////////////////////////////////////////
	@PostMapping("/save")
	public String saveProduct(Product product, RedirectAttributes ra,
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipartFile,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultipartFile,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIds", required = false) String[] imageIds,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopUserDetails loggedUser
			) throws IOException {

		if(loggedUser.hasRole("Salesperson")) {
			productService.saveProductPrice(product);
			
			ra.addFlashAttribute("message", "The product has been saved successfully!");

			return "redirect:/products";
		}
		
		ProductSaveHelper.setMainImageName(mainImageMultipartFile, product);
		ProductSaveHelper.setExistingExtraImageName(imageIds, imageNames, product);
		ProductSaveHelper.setNewExtraImagesName(extraImageMultipartFile, product);
		ProductSaveHelper.setProductDetail(detailNames, detailValues, product);

		Product savedProduct = productService.save(product);

		ProductSaveHelper.saveUploadedImages(mainImageMultipartFile, extraImageMultipartFile, savedProduct);

		ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);

		ra.addFlashAttribute("message", "The product has been saved successfully!");

		return "redirect:/products";
	}

	///////////////////////////////////////////////////////////////
	@GetMapping("/{id}/enabled/{status}")
	public String updateStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {

		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The product id " + id + " has been " + status + " successfully!";

		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/products";
	}

	///////////////////////////////////////////////////////////////
	@GetMapping("/detail/{id}")
	public String viewDetail(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
			Product product = productService.findById(id);

			model.addAttribute("product", product);

			return "products/product_detail_modal";
		} catch (ProductNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/products";
		}
	}
	
	///////////////////////////////////////////////////////////////
	@GetMapping("/edit/{id}")
	public String editCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
			Product product = productService.findById(id);
			
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("product", product);
			model.addAttribute("numberOfExistingExtraImages", product.getImages().size());
			model.addAttribute("productDetails", product.getDetails());
			model.addAttribute("listBrands", brandService.listAll());
			
			return "products/product_form";
		} catch (ProductNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/products";
		}
	}

	///////////////////////////////////////////////////////////////
	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes)
			throws ProductNotFoundException {
		try {
			productService.deleteById(id);
			String productExtraImagesDir = "../product-images/" + id + "/extras";
			String productImagesDir = "../product-images/" + id;

			FileUploadUtil.removeDir(productExtraImagesDir);
			FileUploadUtil.removeDir(productImagesDir);

			redirectAttributes.addFlashAttribute("message",
					"The product with ID " + id + " has been deleted successfully!");
		} catch (ProductNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/products";
	}
}
