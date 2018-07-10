package uk.co.bconline.ndelius.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bconline.ndelius.test.util.AuthUtils.token;

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

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserControllerTest
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
	public void searchQueryIsRequired() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void maxPageSizeForSearchIs100() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "")
				.param("pageSize", "101"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void minPageSizeIs1() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "")
				.param("pageSize", "0"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void searchResultsMatchQuery() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "j blog"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].forenames", is("Jane")))
				.andExpect(jsonPath("$[1].forenames", is("Jim")))
				.andExpect(jsonPath("$[2].forenames", is("Joe")));
	}

	@Test
	public void searchResultsAreLimitedToPageSize() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "j blog")
				.param("pageSize", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void combinedUserIsReturned() throws Exception
	{
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", equalTo("test.user")))
				.andExpect(jsonPath("$.forenames", equalTo("Test")))			// From OID
				.andExpect(jsonPath("$.surname", equalTo("User")))				// From OID
				.andExpect(jsonPath("$.organisation.code", equalTo("NPS")));	// From DB
	}
}