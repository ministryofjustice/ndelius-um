package uk.co.bconline.ndelius.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import uk.co.bconline.ndelius.model.ldap.ADUser;

public abstract class ADUserDetailsService implements UserDetailsService
{
	public abstract Optional<ADUser> getUser(String username);

	@Override
	public UserDetails loadUserByUsername(String username)
	{
		return getUser(stripDomain(username)).orElse(null);
	}

	private static String stripDomain(String username)
	{
		return username.replaceAll("^(.*)@.*$", "$1");
	}

}
