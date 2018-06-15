package uk.co.bconline.ndelius.config.security;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
public class JwtConfigTest
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
		mvc.perform(get("/api/whoami"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Bearer"));
	}

	@Test
	public void invalidCredentialsReturnsUnauthorized() throws Exception
	{
		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer invalid.token.test"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Bearer"));
	}

	@Test
	public void invalidAuthTypeReturnsUnauthorized() throws Exception
	{
		mvc.perform(get("/api/whoami")
				.header("Authorization", "Basic invalid"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Bearer"));
	}

	@Test
	public void successfulAuthWithBearerToken() throws Exception
	{
		String token = mvc.perform(get("/api/login")
				.with(httpBasic("test.user", "secret")))
				.andReturn()
				.getResponse()
				.getCookie("my-cookie").getValue();

		mvc.perform(get("/api/whoami"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Bearer"));

		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(header().doesNotExist("WWW-Authenticate"))
				.andExpect(jsonPath("$.username", is("test.user")));
	}

	@Test
	public void optionsDoesntRequireAuth() throws Exception
	{
		mvc.perform(options("/api/whoami"))
				.andExpect(status().isOk());
	}
}