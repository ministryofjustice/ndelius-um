package uk.co.bconline.ndelius.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
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