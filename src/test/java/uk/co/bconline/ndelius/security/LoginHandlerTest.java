package uk.co.bconline.ndelius.security;

import io.jsonwebtoken.Claims;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.bconline.ndelius.model.auth.UserInteraction;
import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.security.handler.LoginHandler;
import uk.co.bconline.ndelius.util.JwtHelper;

import javax.servlet.http.Cookie;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class LoginHandlerTest
{
	@Autowired
	private LoginHandler handler;

	@Autowired
	private JwtHelper jwtHelper;

	@Test
	public void cookieIsCreatedOnSuccessfulLogin()
	{
		OIDUser oidUser = new OIDUser();
		oidUser.setUsername("test.user");
		AuthenticationToken authentication = new AuthenticationToken(oidUser, null);

		MockHttpServletResponse response = new MockHttpServletResponse();
		handler.onAuthenticationSuccess(null, response, authentication);

		Cookie cookie = response.getCookie("my-cookie");
		assertNotNull(cookie);
		assertEquals("test.user", jwtHelper.parseToken(cookie.getValue()).getSubject());
	}

	@Test
	public void cookieContainsAllowedUserInteractions()
	{
		OIDUser oidUser = new OIDUser();
		oidUser.setUsername("test.user");
		AuthenticationToken authentication = new AuthenticationToken(oidUser, null);

		MockHttpServletResponse response = new MockHttpServletResponse();
		handler.onAuthenticationSuccess(null, response, authentication);

		Cookie cookie = response.getCookie("my-cookie");
		Claims token = jwtHelper.parseToken(cookie.getValue());
		List<UserInteraction> interactions = jwtHelper.getInteractions(token);

		assertNotNull(interactions);
		assertThat(interactions, hasSize(11));
		assertThat(interactions, hasItem(hasProperty("authority", is("UMBI001"))));
	}
}
