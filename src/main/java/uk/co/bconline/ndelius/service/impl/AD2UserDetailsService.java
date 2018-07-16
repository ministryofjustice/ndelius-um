package uk.co.bconline.ndelius.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.repository.ad2.AD2UserRepository;
import uk.co.bconline.ndelius.service.ADUserDetailsService;

@Service
@ConditionalOnProperty("ad.secondary.urls")
public class AD2UserDetailsService extends ADUserDetailsService
{
	private final AD2UserRepository repository;

	@Autowired
	public AD2UserDetailsService(AD2UserRepository repository)
	{
		this.repository = repository;
	}

	@Override
	public Optional<ADUser> getUser(String username)
	{
		return repository.findByUsername(username);
	}
}
