package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.ldap.OIDUser;

import java.util.List;
import java.util.Optional;

public interface OIDUserService
{
	List<String> getUserRoles(String username);
	List<OIDUser> search(String query, int page, int pageSize);
	Optional<OIDUser> getUser(String username);
	String getUserHomeArea(String username);
}
