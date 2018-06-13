package uk.co.bconline.ndelius.security;

import static org.junit.Assert.*;

import javax.jws.Oneway;
import javax.servlet.http.Cookie;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.co.bconline.ndelius.model.OIDUser;
import uk.co.bconline.ndelius.util.JwtHelper;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {
		"jwt.cookie=my-cookie",
		"jwt.secret=secret"
})
public class LoginSuccessHandlerTest
{
	@Autowired
	private LoginSuccessHandler handler;

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