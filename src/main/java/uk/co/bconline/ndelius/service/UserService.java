package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService
{
	boolean usernameExists(String username);
	List<SearchResult> search(String query, int page, int pageSize);
	Optional<User> getUser(String username);
	void addUser(User user);
	void updateUser(String username, User user);
}
