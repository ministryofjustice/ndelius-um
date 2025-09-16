package uk.co.bconline.ndelius.config.security.auth;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bconline.ndelius.test.util.TokenUtils.clientCredentialsToken;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class ClientCredentialsAuthTest
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
	public void invalidClientCredentialsReturnsUnauthorized() throws Exception
	{
		mvc.perform(post("/oauth2/token")
				.with(httpBasic("INVALID", "INVALID"))
				.param("grant_type", "client_credentials"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void successfulClientLoginReturnsBearerToken() throws Exception
	{
		mvc.perform(post("/oauth2/token")
				.with(httpBasic("test.server.client", "secret"))
				.param("grant_type", "client_credentials"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("token_type", is("Bearer")))
				.andExpect(jsonPath("access_token", notNullValue()));
	}

	@Test
	public void accessingASecureEndpointWithAValidTokenIsAllowed() throws Exception
	{
		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer " + clientCredentialsToken(mvc, "test.server.client")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("username", is("test.server.client")));
	}
}
