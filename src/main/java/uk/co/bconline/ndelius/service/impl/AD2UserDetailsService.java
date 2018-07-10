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
import uk.co.bconline.ndelius.repository.ad2.AD2UserRepository;

@Service
@ConditionalOnProperty("ad.secondary.urls")
public class AD2UserDetailsService implements UserDetailsService
{
	private final AD2UserRepository repository;

	@Autowired
	public AD2UserDetailsService(AD2UserRepository repository)
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
