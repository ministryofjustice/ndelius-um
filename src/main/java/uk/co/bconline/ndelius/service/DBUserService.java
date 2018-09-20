package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entity.UserEntity;

public interface DBUserService
{
	Long getMyUserId();
	boolean usernameExists(String username);
	Optional<UserEntity> getUser(String username);
	List<SearchResult> search(String searchTerm);
	UserEntity save(UserEntity user);
}
