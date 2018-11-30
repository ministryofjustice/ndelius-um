package uk.co.bconline.ndelius.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
import uk.co.bconline.ndelius.model.auth.UserInteraction;
import uk.co.bconline.ndelius.util.JwtHelper;

import java.util.Date;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.bconline.ndelius.test.util.AuthUtils.token;

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

	@Autowired
	private JwtHelper jwtHelper;

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
		mvc.perform(get("/api/whoami"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", "Bearer"));

		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer " + token(mvc)))
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

	@Test
	public void tokenIsNotRefreshedBeforeExpiry() throws Exception
	{
		// Create token
		String token = jwtHelper.generateToken("test.user", singletonList(new UserInteraction("ABC")));

		// Attempt request and see no new token is issued
		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(cookie().doesNotExist("my-cookie"));
	}

	@Test
	public void tokenIsRefreshedAfterExpiry() throws Exception
	{
		// Create token
		String token = jwtHelper.generateToken("test.user", singletonList(new UserInteraction("ABC")));

		// Set expiry to 2 hours ago
		Claims claims = jwtHelper.parseToken(token).setExpiration(new Date(now().minus(2, HOURS).toEpochMilli()));
		token = Jwts.builder().setClaims(claims).compact();

		// Attempt request and see new token is issued
		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(cookie().exists("my-cookie"))
				.andExpect(cookie().value("my-cookie", not(token)));
	}

	@Test
	public void tokenIsInvalidAfter24Hours() throws Exception
	{
		// Create token
		String token = jwtHelper.generateToken("test.user", singletonList(new UserInteraction("ABC")));

		// Set expiry to 25 hours ago
		Claims claims = jwtHelper.parseToken(token).setExpiration(new Date(now().minus(25, HOURS).toEpochMilli()));
		token = Jwts.builder().setClaims(claims).compact();

		// Attempt request and see authentication is re-asserted
		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isUnauthorized());
	}
}