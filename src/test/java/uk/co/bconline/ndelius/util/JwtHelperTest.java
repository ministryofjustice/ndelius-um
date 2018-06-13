package uk.co.bconline.ndelius.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {
		"jwt.cookie=my-cookie",
		"jwt.secret=secret"
})
public class JwtHelperTest
{
	@Autowired
	private JwtHelper jwtHelper;

	@Test
	public void tokenCanBeRetrievedFromHeader()
	{
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Authorization")).thenReturn("Bearer abcd");

		String token = jwtHelper.getToken(request);

		assertEquals("abcd", token);
	}

	@Test
	public void tokenCanBeRetrievedFromCookie()
	{
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("my-cookie", "abcd")});

		String token = jwtHelper.getToken(request);

		assertEquals("abcd", token);
	}

	@Test
	public void usernameCanBeParsedFromToken()
	{
		String token = aValidTokenFor("Joe Bloggs");

		String username = jwtHelper.getUsernameFromToken(token);

		assertEquals("Joe Bloggs", username);
	}

	private String aValidTokenFor(String sub)
	{
		return Jwts.builder()
				.setSubject(sub)
				.signWith(SignatureAlgorithm.HS512, "secret")
				.compact();
	}
}