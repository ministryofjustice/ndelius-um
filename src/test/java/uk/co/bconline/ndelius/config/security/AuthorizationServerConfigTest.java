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
import uk.co.bconline.ndelius.test.util.TokenUtils;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
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
public class AuthorizationServerConfigTest {
	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.alwaysDo(print())
				.build();
	}

	@Test
	public void noCredentialsReturnsUnauthorized() throws Exception {
		mvc.perform(post("/oauth/token"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Basic realm=\"ndelius-clients\""));
	}

	@Test
	public void accessingASecureEndpointWithoutATokenIsForbidden() throws Exception {
		mvc.perform(get("/api/user/test.user"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("error_description", is("Full authentication is required to access this resource")));
	}

	@Test
	public void accessingASecureEndpointWithAnInvalidTokenIsUnauthorized() throws Exception {
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer INVALID-TOKEN"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("error_description", is("Invalid access token: INVALID-TOKEN")));
	}

	@Test
	public void accessingASecureEndpointWithAValidTokenIsAllowed() throws Exception {
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("username", is("test.user")));
	}

	@Test
	public void actuatorDoesntRequireAuthentication() throws Exception {
		mvc.perform(get("/actuator/info"))
				.andExpect(status().isOk());
	}

	@Test
	public void tokenCanBeUsedToAuthoriseBothRolesAndInteractions() throws Exception {
		String authCode = TokenUtils.getAuthCode(mvc, "test.user");

		mvc.perform(post("/oauth/token")
				.with(httpBasic("test.web.client", "secret"))
				.param("code", authCode)
				.param("grant_type", "authorization_code")
				.param("redirect_uri", "https://example.com/login-success"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("scope", containsString("UMBT001")))    // Granted role (business transaction)
				.andExpect(jsonPath("scope", containsString("UMBI001")));   // Granted interaction (business interaction)
	}
}