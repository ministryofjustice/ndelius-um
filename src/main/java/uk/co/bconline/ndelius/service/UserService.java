package uk.co.bconline.ndelius.service;

import org.springframework.validation.annotation.Validated;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Validated
public interface UserService
{
	boolean usernameExists(String username);
	List<SearchResult> search(String query, Set<String> groupFilter, Set<String> datasetFilter,
							  boolean includeInactiveUsers, int page, int pageSize);
	Optional<User> getUser(String username);
	Optional<User> getUserByStaffCode(String staffCode);
	void addUser(@Valid User user);
	void updateUser(@Valid User user);
}
