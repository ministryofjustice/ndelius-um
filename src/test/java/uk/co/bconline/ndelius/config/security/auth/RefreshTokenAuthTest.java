package uk.co.bconline.ndelius.config.security.auth;

import com.jayway.jsonpath.JsonPath;
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

import static java.time.Instant.now;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bconline.ndelius.test.util.TokenUtils.getAuthCode;
import static uk.co.bconline.ndelius.util.EncryptionUtils.encrypt;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class RefreshTokenAuthTest
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
	public void tokenCanBeRefreshed() throws Exception
	{
		String refreshToken = JsonPath.read(mvc.perform(post("/oauth/token")
				.with(httpBasic("test.web.client", "secret"))
				.param("code", getAuthCode(mvc, "test.user"))
				.param("grant_type", "authorization_code")
				.param("redirect_uri", "https://example.com/login-success"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString(), "refresh_token");

		mvc.perform(post("/oauth/token")
				.with(httpBasic("test.web.client", "secret"))
				.param("grant_type", "refresh_token")
				.param("refresh_token", refreshToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("access_token", notNullValue()));
	}


	@Test
	public void preAuthenticatedTokenCanBeRefreshed() throws Exception
	{
		String refreshToken = JsonPath.read(mvc.perform(post("/oauth/token")
				.with(httpBasic("test.web.client", "secret"))
				.param("u", encrypt("test.user", "ThisIsASecretKey"))
				.param("t", encrypt(String.valueOf(now().toEpochMilli()), "ThisIsASecretKey"))
				.param("grant_type", "preauthenticated"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString(), "refresh_token");

		mvc.perform(post("/oauth/token")
				.with(httpBasic("test.web.client", "secret"))
				.param("grant_type", "refresh_token")
				.param("refresh_token", refreshToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("access_token", notNullValue()));
	}
}