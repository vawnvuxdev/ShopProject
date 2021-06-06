package com.evanshop.restController;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evanshop.common.entity.Country;
import com.evanshop.common.entity.State;
import com.evanshop.dto.StateDTO;
import com.evanshop.repository.StateRepository;

@RestController
@RequestMapping("/states")
public class StateRestController {

	@Autowired
	private StateRepository stateRepo;

	@GetMapping("/list/{id}")
	public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {
		List<State> states = stateRepo.findByCountryOrderByNameAsc(new Country(countryId));
		List<StateDTO> result = new ArrayList<>();
		for (State s : states) {
			result.add(new StateDTO(s.getId(), s.getName()));
		}
		return result;
	}
}
