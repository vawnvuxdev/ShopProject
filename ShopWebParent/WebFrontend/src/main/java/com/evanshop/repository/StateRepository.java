package com.evanshop.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.evanshop.common.entity.Country;
import com.evanshop.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer> {

	public List<State> findByCountryOrderByNameAsc(Country country);
}
