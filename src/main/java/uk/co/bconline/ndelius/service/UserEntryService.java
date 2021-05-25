package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entry.GroupEntry;
import uk.co.bconline.ndelius.model.entry.UserEntry;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserEntryService
{
	boolean usernameExists(String username);
	List<SearchResult> search(String query, boolean includeInactiveUsers, Set<String> datasets);
	Optional<UserEntry> getBasicUser(String username);
	Optional<UserEntry> getUser(String username);
	String getUserHomeArea(String username);
	Set<GroupEntry> getUserGroups(String username);
	Map<String, UserEntry> export();
	void save(UserEntry user);
	void save(String username, UserEntry user);
}
