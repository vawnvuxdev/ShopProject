package com.evanshop.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.evanshop.common.entity.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
	
	@Query("SELECT c FROM Currency c ORDER BY c.name ASC")
	public List<Currency> findAllOrderByNameAsc();
}
