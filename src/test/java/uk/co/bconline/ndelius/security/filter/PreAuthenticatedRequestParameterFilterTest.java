package uk.co.bconline.ndelius.security.filter;

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
import static java.time.temporal.ChronoUnit.HOURS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bconline.ndelius.util.EncryptionUtils.encrypt;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class PreAuthenticatedRequestParameterFilterTest
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
	public void canLoginWithEncryptedRequestParams() throws Exception
	{
		mvc.perform(post("/oauth/token")
				.param("u", encrypt("test.user", "ThisIsASecretKey"))
				.param("t", encrypt(String.valueOf(now().toEpochMilli()), "ThisIsASecretKey"))
				.param("grant_type", "client_credentials")
				.param("scope", "UMBI001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("token_type", is("bearer")))
				.andExpect(jsonPath("access_token", notNullValue()));
	}

	@Test
	public void missingTimestampIsUnauthorized() throws Exception
	{
		mvc.perform(post("/oauth/token")
				.param("u", encrypt("test.user", "ThisIsASecretKey"))
				.param("grant_type", "client_credentials")
				.param("scope", "UMBI001"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void timestampOutOfDateIsUnauthorized() throws Exception
	{
		mvc.perform(post("/oauth/token")
				.param("u", encrypt("test.user", "ThisIsASecretKey"))
				.param("t", encrypt(String.valueOf(now().minus(2, HOURS).toEpochMilli()), "ThisIsASecretKey"))
				.param("grant_type", "client_credentials")
				.param("scope", "UMBI001"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void incorrectKeyIsUnauthorized() throws Exception
	{
		mvc.perform(post("/oauth/token")
				.param("u", encrypt("test.user", "INVALID-KEY"))
				.param("t", encrypt(String.valueOf(now().toEpochMilli()), "INVALID-KEY"))
				.param("grant_type", "client_credentials")
				.param("scope", "UMBI001"))
				.andExpect(status().isUnauthorized());
	}
}