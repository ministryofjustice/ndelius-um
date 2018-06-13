package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import uk.co.bconline.ndelius.model.OIDUser;
import uk.co.bconline.ndelius.repository.oid.OIDUserRepository;

@Service
public class OIDUserDetailsService implements UserDetailsService
{
	private final OIDUserRepository repository;

	@Autowired
	public OIDUserDetailsService(OIDUserRepository repository)
	{
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		return repository
				.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)));
	}
}
