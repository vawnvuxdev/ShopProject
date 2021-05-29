package com.evanshop.admin.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.evanshop.common.entity.Country;

public interface CountryRepository extends CrudRepository<Country, Integer> {

	public List<Country> findAllByOrderByNameAsc();
}
