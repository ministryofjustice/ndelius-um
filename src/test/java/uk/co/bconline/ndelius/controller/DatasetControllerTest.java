package uk.co.bconline.ndelius.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bconline.ndelius.test.util.AuthUtils.token;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class DatasetControllerTest
{
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private BasicAuthenticationFilter basicAuthenticationFilter;

	@Autowired
	private OncePerRequestFilter jwtAuthenticationFilter;

	private MockMvc mvc;

	@Before
	public void setup()
	{
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.addFilter(jwtAuthenticationFilter)
				.addFilter(basicAuthenticationFilter)
				.alwaysDo(print())
				.build();
	}

	@Test
	public void datasetsAreReturned() throws Exception
	{
		mvc.perform(get("/api/datasets")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(6)))
				.andExpect(jsonPath("$[*].code", hasItems("N01", "N02", "N03", "C01", "C02", "C03")));
	}

	@Test
	public void subContractedProvidersAreReturned() throws Exception
	{
		mvc.perform(get("/api/dataset/N01/subContractedProviders")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[*].code", hasItems("N01SC1", "N01SC2", "N01SC3")));
	}
}