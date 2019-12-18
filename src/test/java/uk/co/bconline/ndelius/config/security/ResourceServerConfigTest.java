package uk.co.bconline.ndelius.config.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.bconline.ndelius.test.util.TokenUtils.token;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class ResourceServerConfigTest
{
	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup()
	{
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.alwaysDo(print())
				.build();
	}

	@Test
	public void noCredentialsReturnsUnauthorized() throws Exception
	{
		mvc.perform(post("/oauth/token"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Basic realm=\"ndelius-oauth\""));
	}

	@Test
	public void invalidCredentialsReturnsUnauthorized() throws Exception
	{
		mvc.perform(post("/oauth/token")
				.with(httpBasic("INVALID", "INVALID"))
				.param("grant_type", "client_credentials")
				.param("scope", "UMBI001"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Basic realm=\"ndelius-oauth\""));
	}

	@Test
	public void successfulLoginReturnsBearerToken() throws Exception
	{
		mvc.perform(post("/oauth/token")
				.with(httpBasic("test.user", "secret"))
				.param("grant_type", "client_credentials")
				.param("scope", "UMBI001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("token_type", is("bearer")))
				.andExpect(jsonPath("access_token", notNullValue()));
	}

	@Test
	public void accessingASecureEndpointWithoutATokenIsForbidden() throws Exception
	{
		mvc.perform(get("/api/user/test.user"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("error_description", is("Full authentication is required to access this resource")));
	}

	@Test
	public void accessingASecureEndpointWithAnInvalidTokenIsUnauthorized() throws Exception
	{
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer INVALID-TOKEN"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("error_description", is("Invalid access token: INVALID-TOKEN")));
	}

	@Test
	public void accessingASecureEndpointWithAValidTokenIsAllowed() throws Exception
	{
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("username", is("test.user")));
	}

	@Test
	public void actuatorDoesntRequireAuthentication() throws Exception
	{
		mvc.perform(get("/actuator/info"))
				.andExpect(status().isOk());
	}
}