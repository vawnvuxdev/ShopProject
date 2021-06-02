package com.evanshop.admin.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.evanshop.admin.repository.CountryRepository;
import com.evanshop.admin.repository.StateRepository;
import com.evanshop.common.entity.Country;
import com.evanshop.common.entity.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private StateRepository stateRepo;

	@Autowired
	private CountryRepository countryRepo;

	@Test
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	public void testListByCountry() throws Exception {
		Integer vn = 4;
		String url = "/states/list/" + vn;

		MvcResult result = mockMvc.perform(get(url))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();

		String jsonResponse = result.getResponse().getContentAsString();

		State[] states = mapper.readValue(jsonResponse, State[].class);

		for (State s : states) {
			System.out.println(s);
		}

		assertThat(states).hasSizeGreaterThan(1);
	}

	@Test
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	public void testCreateState() throws JsonProcessingException, Exception {
		String url = "/states/save";
		Country vn  = countryRepo.findById(4).get();
		State state = new State("Ha nam", vn);
			
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(mapper.writeValueAsString(state))
				.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		String resposne = result.getResponse().getContentAsString();

		Integer stateId = Integer.parseInt(resposne);

		Optional<State> findById = stateRepo.findById(stateId);
		assertThat(findById.isPresent());

		State savedState = findById.get();

		assertThat(savedState.getName()).isEqualTo("Ha nam");
	}

	@Test
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	public void testUpdateCountry() throws JsonProcessingException, Exception {
		String url = "/states/save";
		State state = stateRepo.findById(8).get();
		state.setName("Da Nang");
		
		mockMvc.perform(post(url).contentType("application/json")
				.content(mapper.writeValueAsString(state))
				.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		State updatedState = stateRepo.findById(8).get();
		assertThat(updatedState.getName()).isEqualTo("Da Nang");
	}

	@Test
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	public void testDelete() throws Exception {
		Integer stateId = 4;
		String url = "/states/delete/" + stateId;

		mockMvc.perform(get(url)).andExpect(status().isOk());

		Optional<State> findById = stateRepo.findById(stateId);
		assertThat(findById).isNotPresent();
	}
}
