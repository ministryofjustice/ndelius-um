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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.bconline.ndelius.test.util.TokenUtils.implicitToken;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class ImplicitAuthTest
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
	public void invalidUserCredentialsReturnsUnauthorized() throws Exception
	{
		mvc.perform(get("/oauth/authorize")
				.with(httpBasic("INVALID", "INVALID"))
				.param("client_id", "test.web.client")
				.param("response_type", "token"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Basic realm=\"ndelius-users\""));
	}

	@Test
	public void successfulLoginRedirectsWithAccessTokenInFragment() throws Exception
	{
		mvc.perform(get("/oauth/authorize")
				.with(httpBasic("test.user", "secret"))
				.param("client_id", "test.web.client")
				.param("response_type", "token")
				.param("redirect_uri", "https://example.com/login-success"))
				.andExpect(status().isFound())
				.andExpect(header().string("Location", containsString("#access_token=")));
	}

	@Test
	public void accessingASecureEndpointWithAValidTokenIsAllowed() throws Exception
	{
		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer " + implicitToken(mvc, "test.user")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("username", is("test.user")));
	}
}