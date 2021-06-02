package com.evanshop.admin.restController;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evanshop.admin.dto.StateDTO;
import com.evanshop.admin.repository.StateRepository;
import com.evanshop.common.entity.Country;
import com.evanshop.common.entity.State;

@RestController
@RequestMapping("/states")
public class StateRestController {

	@Autowired
	private StateRepository stateRepo;

	@GetMapping("/list/{id}")
	public List<StateDTO> listAll(@PathVariable("id") Integer countryId) {
		List<State> states = stateRepo.findByCountryOrderByNameAsc(new Country(countryId));
		List<StateDTO> result = new ArrayList<>();
		for (State s : states) {
			result.add(new StateDTO(s.getId(), s.getName()));
		}
		return result;
	}
	
	@PostMapping("/save")
	public String saveState(@RequestBody State state) {
		State savedState = stateRepo.save(state);
		return String.valueOf(savedState.getId());
	}
	
	@GetMapping("/delete/{id}")
	public void deleteState(@PathVariable("id") Integer id) {
		stateRepo.deleteById(id);
	}
	
}
