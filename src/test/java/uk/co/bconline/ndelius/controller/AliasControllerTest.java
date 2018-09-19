package uk.co.bconline.ndelius.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bconline.ndelius.model.UserValidationTest.aValidUser;
import static uk.co.bconline.ndelius.test.util.AuthUtils.token;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.bconline.ndelius.model.Alias;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class AliasControllerTest
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
	public void aliasUsernameMismatch() throws Exception
	{
		mvc.perform(post("/api/alias/test.user123")
				.header("Authorization", "Bearer " + token(mvc))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules()
						.writeValueAsString(new Alias(
								"test.user",
								"test.user.alias"))))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void successfulAliasCreation() throws Exception
	{
		String token = token(mvc);

		// Create user without alias:
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username("alias.test")
						.build())))
				.andExpect(status().isCreated());

		// Add alias
		mvc.perform(post("/api/alias/alias.test")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(new Alias(
						"alias.test",
						"alias.test123"))))
				.andExpect(status().isNoContent());

		// Assert created
		mvc.perform(get("/api/user/alias.test")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is("alias.test")))
				.andExpect(jsonPath("$.aliasUsername", is("alias.test123")));
	}
}