package com.evanshop.common.entity;

import javax.persistence.*;

import org.springframework.data.annotation.Transient;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 128, nullable = false, unique = true)
	private String name;

	@Column(length = 64, nullable = false, unique = true)
	private String alias;

	@Column(length = 128, nullable = false)
	private String image;

	private boolean enabled;
	
	@Column(name="all_parent_ids", length = 256, nullable = true)
	private String allParentIds;

	@OneToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	@OneToMany(mappedBy = "parent", cascade = { CascadeType.ALL })
	@OrderBy("name asc")
	private Set<Category> children = new HashSet<>();

	public Category() {
	}

	public Category(Integer id, String name, String alias, String image, boolean enabled) {
		this.id = id;
		this.name = name;
		this.alias = alias;
		this.image = image;
		this.enabled = enabled;
	}

	public Category(Integer id, String name, String alias) {
		this.id = id;
		this.name = name;
		this.alias = alias;
	}

	public Category(Integer id) {
		this.id = id;
	}

	public Category(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Category(String name) {
		this.name = name;
		this.alias = name;
		this.image = "default-image.jpg";
	}

	public Category(String name, Category parent) {
		this(name);
		this.parent = parent;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}

	public String getAllParentIds() {
		return allParentIds;
	}

	public void setAllParentIds(String allParentIds) {
		this.allParentIds = allParentIds;
	}

	@Transient
	public String getImagePath() {
		if (this.id == null)
			return "/images/default-thumbnail.jpg";
		return "/category-images/" + this.id + "/" + this.image;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
