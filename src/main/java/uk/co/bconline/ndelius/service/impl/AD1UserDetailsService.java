package uk.co.bconline.ndelius.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.repository.ad1.AD1UserRepository;
import uk.co.bconline.ndelius.service.ADUserDetailsService;

@Service
@ConditionalOnProperty("ad.primary.urls")
public class AD1UserDetailsService extends ADUserDetailsService
{
	private final AD1UserRepository repository;

	@Autowired
	public AD1UserDetailsService(AD1UserRepository repository)
	{
		this.repository = repository;
	}

	@Override
	public Optional<ADUser> getUser(String username)
	{
		return repository.findByUsername(username);
	}
}
