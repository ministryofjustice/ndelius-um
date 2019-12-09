package uk.co.bconline.ndelius.config.security;

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

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.bconline.ndelius.test.util.TokenUtils.token;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class BasicAuthConfigTest
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
	public void noCredentialsReturnsUnauthorized() throws Exception
	{
		mvc.perform(post("/api/login"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Basic realm=\"ndelius-um\""));
	}

	@Test
	public void invalidCredentialsReturnsUnauthorized() throws Exception
	{
		mvc.perform(post("/api/login")
				.header("Authorization", "Basic invalid"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Basic realm=\"ndelius-um\""));
	}

	@Test
	public void successfulLoginReturnsBearerToken() throws Exception
	{
		mvc.perform(post("/api/login")
				.with(httpBasic("test.user", "secret")))
				.andExpect(status().isOk())
				.andExpect(header().doesNotExist("WWW-Authenticate"))
				.andExpect(cookie().exists("my-cookie"))
				.andExpect(jsonPath("$.token", notNullValue()))
				.andExpect(jsonPath("$.token", not("")));
	}

	@Test
	public void invalidAuthTypeReturnsUnauthorized() throws Exception
	{
		mvc.perform(post("/api/login")
				.header("Authorization", "Bearer invalid"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Basic realm=\"ndelius-um\""));
	}

	@Test
	public void bearerTokenAuthIsAllowedOnLoginEndpoint() throws Exception
	{
		mvc.perform(post("/api/login")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(header().doesNotExist("WWW-Authenticate"));
	}

	@Test
	public void optionsDoesntRequireAuth() throws Exception
	{
		mvc.perform(options("/api/login"))
				.andExpect(status().isOk());
	}
}