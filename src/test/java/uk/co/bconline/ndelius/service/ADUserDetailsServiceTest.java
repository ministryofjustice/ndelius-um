package uk.co.bconline.ndelius.service;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;

import uk.co.bconline.ndelius.model.ldap.ADUser;

public class ADUserDetailsServiceTest
{
	private ADUserDetailsService service = new ADUserDetailsService()
	{
		@Override
		public Optional<ADUser> getUser(String username)
		{
			ADUser user = new ADUser();
			user.setUsername(username);
			return Optional.of(user);
		}
	};

	@Test
	public void domainIsStrippedFromUsernameOnLookup()
	{
		UserDetails user = service.loadUserByUsername("test.user@DOMAIN.COM");
		assertEquals("test.user", user.getUsername());
	}

}