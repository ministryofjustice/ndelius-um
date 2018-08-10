package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.ldap.OIDUser;

public interface OIDUserService
{
	List<SearchResult> search(String query);
	List<SearchResult> search(String query, List<String> excludedUsernames);
	Optional<OIDUser> getUser(String username);
	Optional<String> getAlias(String username);
	String getUserHomeArea(String username);
	void save(OIDUser user);
}
