package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.ldap.OIDUser;

public interface OIDUserService
{
	boolean usernameExists(String username);
	List<SearchResult> search(String query);
	Optional<OIDUser> getBasicUser(String username);
	Optional<OIDUser> getUser(String username);
	Optional<String> getAlias(String username);
	Optional<String> getUsernameByAlias(String aliasUsername);
	String getUserHomeArea(String username);
	void save(OIDUser user);
}
