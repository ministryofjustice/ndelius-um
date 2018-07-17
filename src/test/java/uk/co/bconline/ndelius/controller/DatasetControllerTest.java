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

import static org.hamcrest.Matchers.equalTo;
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
				.andExpect(jsonPath("$[0].code", equalTo("N01")))
				.andExpect(jsonPath("$[0].active", equalTo(true)))
				.andExpect(jsonPath("$[0].organisation.code", equalTo("NPS")))
				.andExpect(jsonPath("$[1].code", equalTo("N02")))
				.andExpect(jsonPath("$[1].active", equalTo(true)))
				.andExpect(jsonPath("$[1].organisation.code", equalTo("NPS")))
				.andExpect(jsonPath("$[2].code", equalTo("N03")))
				.andExpect(jsonPath("$[2].active", equalTo(true)))
				.andExpect(jsonPath("$[2].organisation.code", equalTo("NPS")))
				.andExpect(jsonPath("$[3].code", equalTo("C01")))
				.andExpect(jsonPath("$[3].active", equalTo(true)))
				.andExpect(jsonPath("$[3].organisation.code", equalTo("PO1")))
				.andExpect(jsonPath("$[4].code", equalTo("C02")))
				.andExpect(jsonPath("$[4].active", equalTo(true)))
				.andExpect(jsonPath("$[4].organisation.code", equalTo("PO1")))
				.andExpect(jsonPath("$[5].code", equalTo("C03")))
				.andExpect(jsonPath("$[5].active", equalTo(true)))
				.andExpect(jsonPath("$[5].organisation.code", equalTo("PO2")));
	}
}