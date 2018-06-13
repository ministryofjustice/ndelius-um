package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.repository.ad1.AD1UserRepository;
import uk.co.bconline.ndelius.repository.ad2.AD2UserRepository;

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
		return repository
				.findByUsername(name)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", name)));
	}

	private String stripDomain(String username)
	{
		return username.replaceAll("^(.*)@.*$", "$1");
	}
}
