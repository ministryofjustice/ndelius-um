package uk.co.bconline.ndelius.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.servlet.http.Cookie;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.util.JwtHelper;

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
		oidUser.setUsername("test.user123");
		AuthenticationToken authentication = new AuthenticationToken(oidUser, null);

		MockHttpServletResponse response = new MockHttpServletResponse();
		handler.onAuthenticationSuccess(null, response, authentication);

		Cookie cookie = response.getCookie("my-cookie");
		assertNotNull(cookie);
		String expectedToken = jwtHelper.generateToken("test.user123");
		assertEquals(expectedToken, cookie.getValue());
	}
}