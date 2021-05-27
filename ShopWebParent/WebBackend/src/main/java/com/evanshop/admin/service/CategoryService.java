package com.evanshop.admin.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.evanshop.admin.repository.CategoryRepository;
import com.evanshop.common.entity.Category;
import com.evanshop.common.exception.CategoryNotFoundException;

@Service
@Transactional
public class CategoryService {

	public static final int ROOT_CATEGORIES_PER_PAGE = 4;

	@Autowired
	private CategoryRepository catgRepo;

//	public List<Category> listAll(String sortDir) {
//		Sort sort = Sort.by("name");
//		
//		if (sortDir.equals("asc")) {
//			sort = sort.ascending();
//		} else if (sortDir.equals("desc")) {
//			sort = sort.descending();
//		}
//
//		List<Category> rootCategories = catgRepo.findRootCategories(sort);
//
//		return listHierarchicalCategories(rootCategories, sortDir);
//	}

	// List all category by Pageable
	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
		Sort sort = Sort.by("name");

		if (sortDir.equals("asc")) {
			sort = sort.ascending();
		} else if (sortDir.equals("desc")) {
			sort = sort.descending();
		}

		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

		Page<Category> pageCategories = null;
		
		if (keyword != null && !keyword.isEmpty()) {
			pageCategories = catgRepo.searchByName(keyword, pageable);
		} else {
			pageCategories = catgRepo.findRootCategories(pageable);
		}
		
		List<Category> rootCategories = pageCategories.getContent();

		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());

		if (keyword != null && !keyword.isEmpty()) {
			List<Category> searchResult = pageCategories.getContent();
			return searchResult;
		} else {
			return listHierarchicalCategories(rootCategories, sortDir);
		}
	}

	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();

		for (Category rootCatg : rootCategories) {
			Category rootCatgCopy = new Category(rootCatg.getId(), rootCatg.getName(), rootCatg.getAlias(),
					rootCatg.getImage(), rootCatg.isEnabled());

			hierarchicalCategories.add(rootCatgCopy);

			Set<Category> children = sortSubCategories(rootCatg.getChildren(), sortDir);
			for (Category subCatg : children) {
				Category subCatgCopy = new Category(subCatg.getId(), ("--" + subCatg.getName()), subCatg.getAlias(),
						subCatg.getImage(), subCatg.isEnabled());

				hierarchicalCategories.add(subCatgCopy);

				listSubHierarchicalCategories(hierarchicalCategories, subCatg, 1, sortDir);
			}
		}

		return hierarchicalCategories;
	}

	private void listSubHierarchicalCategories(List<Category> listHierarchicalCategories, Category parent, int subLevel,
			String sortDir) {
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		int newSubLevel = subLevel + 1;

		for (Category subCatg : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCatg.getName();

			Category subCatgCopy = new Category(subCatg.getId(), name, subCatg.getAlias(), subCatg.getImage(),
					subCatg.isEnabled());

			listHierarchicalCategories.add(subCatgCopy);

			listSubHierarchicalCategories(listHierarchicalCategories, subCatg, newSubLevel, sortDir);
		}
	}

	public Category save(Category category) {
		Category parent = category.getParent();
		if(parent!=null) {
			String allParentIds = parent.getAllParentIds() == null ? "-" : parent.getAllParentIds();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIds(allParentIds);
		}
		
		return catgRepo.save(category);
	}

	public List<Category> categoriesUseInForm() {
		List<Category> categoriesUseInForm = new ArrayList<>();

		Iterable<Category> categoriesInDB = catgRepo.findRootCategories(Sort.by("name").ascending());

		for (Category category : categoriesInDB) {
			if (category.getParent() == null) {
				categoriesUseInForm.add(new Category(category.getId(), category.getName()));

				Set<Category> childrens = sortSubCategories(category.getChildren());

				for (Category subCategory : childrens) {
					String name = "--" + subCategory.getName();
					categoriesUseInForm.add(new Category(subCategory.getId(), name));
					listSubCatgUsedInForm(categoriesUseInForm, subCategory, 1);
				}
			}
		}
		return categoriesUseInForm;
	}

	public void listSubCatgUsedInForm(List<Category> categoriesUseInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren());

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();

			categoriesUseInForm.add(new Category(subCategory.getId(), name));

			listSubCatgUsedInForm(categoriesUseInForm, subCategory, newSubLevel);
		}
	}

	public Category findById(Integer id) throws CategoryNotFoundException {
		try {
			Category category = catgRepo.findById(id).get();
			return category;
		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not find category with id: " + id);
		}
	}

	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);

//		Category catgByName = catgRepo.findByName(name);  

		if (isCreatingNew) {
			if (catgRepo.findByName(name) != null) {
				return "DuplicateName";
			} else {
				if (catgRepo.findByAlias(alias) != null) {
					return "DuplicateAlias";
				}
			}
		} else {
			if (catgRepo.findByName(name) != null && catgRepo.findByName(name).getId() != id) {
				return "DuplicateName";
			}

			if (catgRepo.findByAlias(alias) != null && catgRepo.findByAlias(alias).getId() != id) {
				return "DuplicateAlias";
			}
		}

		return "OK";
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc");
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category o1, Category o2) {
				if (sortDir.equals("asc")) {
					return o1.getName().compareTo(o2.getName());
				} else {
					return o2.getName().compareTo(o1.getName());
				}
			}

		});

		sortedChildren.addAll(children);

		return sortedChildren;

	}

	public void updateCatgEnabledStatus(Integer id, boolean enabled) {
		catgRepo.updateEnabledStatus(id, enabled);
	}

	public void deleteById(Integer id) throws CategoryNotFoundException {
		Long countById = catgRepo.countById(id);

		if (countById == null || countById == 0) {
			throw new CategoryNotFoundException("Cound not find category with id " + id);
		}

		catgRepo.deleteById(id);
	}

	public List<Category> findAll() {
		return (List<Category>) catgRepo.findAll();
	}

}
