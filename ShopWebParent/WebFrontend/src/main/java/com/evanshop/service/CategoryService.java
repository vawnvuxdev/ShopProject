package com.evanshop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evanshop.common.entity.Category;
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
	
	public Category getCategoryByAlias(String alias) {
		return repo.findByAliasEnabled(alias);
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
