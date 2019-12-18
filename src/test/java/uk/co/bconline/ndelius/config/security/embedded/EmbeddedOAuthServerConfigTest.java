package uk.co.bconline.ndelius.config.security.embedded;

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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("embedded-oauth")
@SpringBootTest({"spring.ldap.urls=ldap://localhost:3061", "spring.ldap.embedded.port=3061"})
public class EmbeddedOAuthServerConfigTest {

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
				.andExpect(header().string("WWW-Authenticate", "Basic realm=\"embedded-oauth\""));
	}

	@Test
	public void canAuthenticateAsTestUser() throws Exception
	{
		String token = JsonPath.read(mvc.perform(post("/oauth/token")
				.with(httpBasic("test.user", "secret"))
				.param("grant_type", "client_credentials")
				.param("grant_type", "refresh_token")
				.param("scope", "UMBI001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("token_type", is("bearer")))
				.andExpect(jsonPath("access_token", notNullValue()))
				.andReturn()
				.getResponse()
				.getContentAsString(), "access_token");

		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("username", is("test.user")));
	}

	@Test
	public void canAuthenticateAsAnotherUser() throws Exception
	{
		String token = JsonPath.read(mvc.perform(post("/oauth/token")
				.with(httpBasic("another.user", "secret"))
				.param("grant_type", "client_credentials")
				.param("scope", "UMBI001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("token_type", is("bearer")))
				.andExpect(jsonPath("access_token", notNullValue()))
				.andReturn()
				.getResponse()
				.getContentAsString(), "access_token");

		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
}