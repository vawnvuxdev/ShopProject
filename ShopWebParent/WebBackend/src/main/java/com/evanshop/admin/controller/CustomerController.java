package com.evanshop.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.evanshop.admin.service.CustomerService;
import com.evanshop.common.entity.Country;
import com.evanshop.common.entity.Customer;
import com.evanshop.common.exception.CustomerNotFoundException;

@Controller
@RequestMapping("/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@GetMapping("")
	public String listFirstPage(Model model) {
		return listByPage(model, 1, "firstName", "asc", null);
	}

	@GetMapping("/page/{pageNum}")
	public String listByPage(Model model, @PathVariable("pageNum") int pageNum, @Param("sortField") String sortField,
			@Param("sortDir") String sortDir, @Param("keyword") String keyword) {

		Page<Customer> page = customerService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Customer> listCustomers = page.getContent();

		long startCount = (pageNum - 1) * CustomerService.CUSTOMERS_PER_PAGE + 1;
		long endCount = startCount + CustomerService.CUSTOMERS_PER_PAGE - 1;

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
		model.addAttribute("listCustomers", listCustomers);

		return "customers/customers";
	}

	@GetMapping("/{id}/enabled/{status}")
	public String updateCustomerStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes ra) {
		customerService.enabledCustomer(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The Customer with ID: " + id + "has been" + status;
		ra.addFlashAttribute("message", message);
		return "redirect:/customers";
	}

	@GetMapping("/edit/{id}")
	public String editCustomer(Model model, @PathVariable("id") Integer id, RedirectAttributes ra) {
		try {
			Customer customer = customerService.getById(id);
			List<Country> countries = customerService.listAllCountries();

			model.addAttribute("listCountries", countries);
			model.addAttribute("customer", customer);
			model.addAttribute("pageTitle", String.format("Edit customer (ID: %d)", id));

			return "customers/customers_form";

		} catch (CustomerNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
			return "redirect:/customers";
		}
	}

	@PostMapping("/save")
	public String saveCustomer(Customer customer, Model model, RedirectAttributes ra) {
		customerService.save(customer);
		ra.addFlashAttribute("message", "The customer has saved !");
		return "redirect:/customers";
	}

	@GetMapping("/delete/{id}")
	public String deleteCustomer(@PathVariable("id") Integer id, RedirectAttributes ra) {
		try {
			customerService.delete(id);
			ra.addFlashAttribute("message", "The customer with ID: " +id + " has been deleted!");
		} catch (CustomerNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
		}
		
 		 
		return "redirect:/customers";
	}
}
