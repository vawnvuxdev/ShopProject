package com.evanshop.admin.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.evanshop.admin.repository.CountryRepository;
import com.evanshop.common.entity.Country;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTest {

	@Autowired
	CountryRepository repository;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@Test
	@WithMockUser(username = "adminuser@admin.net", password = "afdadfasdf", roles = "ADMIN")
	public void testListAll() throws Exception {
		String url = "/countries/list";

		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

		String jsonResponse = result.getResponse().getContentAsString();
		System.out.println(jsonResponse);

		Country[] countries = mapper.readValue(jsonResponse, Country[].class);

		for (Country c : countries) {
			System.out.println(c);
		}

		assertThat(countries).hasSizeGreaterThan(0);
	}

	@Test
	@WithMockUser(username = "adminuser@admin.net", password = "adminadmin", roles = "ADMIN")
	public void testCreateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";
		String cName = "German";
		String cCode = "DE";

		Country country = new Country(cName, cCode);

		MvcResult result = mockMvc.perform(
				post(url).contentType("application/json").content(mapper.writeValueAsString(country)).with(csrf()))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		String resposne = result.getResponse().getContentAsString();

		Integer countryId = Integer.parseInt(resposne);

		Optional<Country> findById = repository.findById(countryId);
		assertThat(findById.isPresent());

		Country savedCountry = findById.get();

		assertThat(savedCountry.getName()).isEqualTo(cName);
	}

	@Test
	@WithMockUser(username = "adminuser@admin.net", password = "adminadmin", roles = "ADMIN")
	public void testUpdateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";
		Integer cId = 6;
		String cName = "China";
		String cCode = "CN";

		Country country = new Country(cId, cName, cCode);

		mockMvc.perform(post(url).contentType("application/json")
				.content(mapper.writeValueAsString(country))
				.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(String.valueOf(cId)));

		Optional<Country> findById = repository.findById(cId);
		assertThat(findById.isPresent());

		Country savedCountry = findById.get();

		assertThat(savedCountry.getName()).isEqualTo(cName);
	}
	
	@Test
	@WithMockUser(username = "adminuser@admin.net", password = "adminadmin", roles = "ADMIN")
	public void testDelete() throws Exception {
		Integer countryId = 6;
		String url = "/countries/delete/" + countryId;
		
		mockMvc.perform(get(url)).andExpect(status().isOk());
		
		Optional<Country> findById = repository.findById(countryId);
		assertThat(findById).isNotPresent();
	}

}
