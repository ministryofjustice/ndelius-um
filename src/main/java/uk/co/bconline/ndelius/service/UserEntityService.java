package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserEntityService
{
	Long getMyUserId();
	boolean usernameExists(String username);
	Optional<UserEntity> getUser(String username);
	Optional<UserEntity> getUserByStaffCode(String staffCode);
	List<SearchResult> search(String searchTerm, boolean includeInactiveUsers, Set<String> datasets);
	UserEntity save(UserEntity user);
}