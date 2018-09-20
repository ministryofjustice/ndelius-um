package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;

public interface UserService
{
	boolean usernameExists(String username);
	List<SearchResult> search(String query, int page, int pageSize);
	Optional<User> getUser(String username);
	void addUser(User user);
	void updateUser(User user);
}
