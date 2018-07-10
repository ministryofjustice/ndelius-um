package uk.co.bconline.ndelius.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.val;
import uk.co.bconline.ndelius.model.ADUser;
import uk.co.bconline.ndelius.repository.ad1.AD1UserRepository;

@Service
@ConditionalOnProperty("ad.primary.urls")
public class AD1UserDetailsService implements UserDetailsService
{
	private final AD1UserRepository repository;

	@Autowired
	public AD1UserDetailsService(AD1UserRepository repository)
	{
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		val name = stripDomain(username);
		return getUser(name).orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", name)));
	}

	public Optional<ADUser> getUser(String username)
	{
		return repository.findByUsername(username);
	}

	private String stripDomain(String username)
	{
		return username.replaceAll("^(.*)@.*$", "$1");
	}
}
