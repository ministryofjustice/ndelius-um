package uk.co.bconline.ndelius.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;

@WithMockUser
@ContextConfiguration
@RunWith(SpringRunner.class)
public class AuthUtilsTest
{
	@Test
	public void returnsUserDetailsFromSecurityContext()
	{
		UserDetails me = AuthUtils.me();
		assertEquals("user", me.getUsername());
		assertEquals("password", me.getPassword());
	}

	@Test
	public void usernameIsReturned()
	{
		assertEquals("user", AuthUtils.myUsername());
	}

	@Test
	public void rolesAreReturned()
	{
		assertThat(AuthUtils.myInteractions().collect(toSet()), hasItem("ROLE_USER"));
	}

	@Test
	public void checkNationalAccessIsFalse()
	{
		assertFalse(AuthUtils.isNational());
	}

	@Test
	@WithMockUser(authorities = "UABI025")
	public void checkNationalAccessIsTrue()
	{
		assertTrue(AuthUtils.isNational());
	}
}
