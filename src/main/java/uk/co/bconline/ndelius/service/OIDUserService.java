package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.ldap.OIDUser;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OIDUserService
{
	boolean usernameExists(String username);
	List<SearchResult> search(String query, Set<String> datasets);
	Optional<OIDUser> getBasicUser(String username);
	Optional<OIDUser> getUser(String username);
	String getUserHomeArea(String username);
	void save(OIDUser user);
	void save(String username, OIDUser user);
}
