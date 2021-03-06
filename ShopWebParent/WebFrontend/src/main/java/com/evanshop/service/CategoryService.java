package com.evanshop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evanshop.common.entity.Category;
import com.evanshop.common.exception.CategoryNotFoundException;
import com.evanshop.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired CategoryRepository repo;

	public List<Category> listNoChildrenCateogries(){
		List<Category> listNoChildrenCateogries = new ArrayList<>();
		
		List<Category> listEnabledCategories = repo.findAllEnabled();
		listEnabledCategories.forEach(c -> {
			Set<Category> children = c.getChildren();
			if (children == null || children.size() == 0) {
				listNoChildrenCateogries.add(c);
			}
		});
		
		return listNoChildrenCateogries;
	}
	
	public Category getCategoryByAlias(String alias) throws CategoryNotFoundException {
		Category category = repo.findByAliasEnabled(alias);
		if(category == null) {
			throw new CategoryNotFoundException("Could not found Category !");
		}
		return category;
	}
	
	public List<Category> getCategoryParents(Category child){
		List<Category> listParents = new ArrayList<>();
		
		Category parent = child.getParent();
		
		while (parent != null) {
			listParents.add(0, parent);
			parent = parent.getParent(); 
		}
		
		listParents.add(child);
		
		return listParents;
	}
	
}
