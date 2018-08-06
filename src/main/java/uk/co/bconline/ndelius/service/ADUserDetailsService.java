package uk.co.bconline.ndelius.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.repository.ad.ADUserRepository;

public abstract class ADUserDetailsService implements UserDetailsService
{
	public abstract ADUserRepository getRepository();

	@Override
	public UserDetails loadUserByUsername(String username)
	{
		return getUser(stripDomain(username))
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)));
	}

	private static String stripDomain(String username)
	{
		return username.replaceAll("^(.*)@.*$", "$1");
	}

	public Optional<ADUser> getUser(String username)
	{
		return getRepository().findByUsername(username);
	}

	public void save(ADUser adUser)
	{
		getRepository().save(adUser);
	}

}
