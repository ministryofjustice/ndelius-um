package uk.co.bconline.ndelius.service;

import java.util.Optional;

import uk.co.bconline.ndelius.model.User;

public interface UserService
{
	Optional<User> getUser(String username);
}
