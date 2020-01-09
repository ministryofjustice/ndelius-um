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

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.bconline.ndelius.test.util.TokenUtils.authCodeToken;
import static uk.co.bconline.ndelius.test.util.TokenUtils.getAuthCode;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class AuthorizationCodeAuthTest
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
				.param("response_type", "code"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Basic realm=\"ndelius-users\""));
	}

	@Test
	public void successfulLoginRedirectsWithAuthorizationCodeInQueryParams() throws Exception
	{
		mvc.perform(get("/oauth/authorize")
				.with(httpBasic("test.user", "secret"))
				.param("client_id", "test.web.client")
				.param("response_type", "code")
				.param("redirect_uri", "https://example.com/login-success"))
				.andExpect(status().isFound())
				.andExpect(header().string("Location", containsString("?code=")));
	}

	@Test
	public void invalidClientLoginIsUnauthorized() throws Exception
	{
		String authCode = getAuthCode(mvc, "test.user");

		mvc.perform(get("/oauth/token")
				.with(httpBasic("INVALID", "INVALID"))
				.param("code", authCode)
				.param("grant_type", "authorization_code"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Basic realm=\"ndelius-clients\""));
	}

	@Test
	public void authorizationCodeCanBeSwappedForAccessToken() throws Exception
	{
		String authCode = getAuthCode(mvc, "test.user");

		mvc.perform(post("/oauth/token")
				.with(httpBasic("test.web.client", "secret"))
				.param("code", authCode)
				.param("grant_type", "authorization_code")
				.param("redirect_uri", "https://example.com/login-success"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("token_type", is("bearer")))
				.andExpect(jsonPath("access_token", notNullValue()));
	}

	@Test
	public void accessingASecureEndpointWithAValidTokenIsAllowed() throws Exception
	{
		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer " + authCodeToken(mvc, "test.user")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("username", is("test.user")));
	}

	@Test
	public void pathBasedRedirectUriCanBeUsed() throws Exception
	{
		mvc.perform(get("/oauth/authorize")
				.with(httpBasic("test.user", "secret"))
				.param("client_id", "test.web.client")
				.param("redirect_uri", "/login-success")
				.param("response_type", "code"))
				.andExpect(status().isFound())
				.andExpect(header().string("Location", startsWith("/login-success")));
	}
}