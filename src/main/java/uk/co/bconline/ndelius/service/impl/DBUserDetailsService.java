package uk.co.bconline.ndelius.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.repository.db.UserEntityRepository;

@Service
public class DBUserDetailsService
{
	private final UserEntityRepository repository;

	@Autowired
	public DBUserDetailsService(UserEntityRepository repository)
	{
		this.repository = repository;
	}

	public Optional<UserEntity> getUser(String username)
	{
		return repository.getUserEntityByUsernameEqualsIgnoreCase(username);
	}
}
