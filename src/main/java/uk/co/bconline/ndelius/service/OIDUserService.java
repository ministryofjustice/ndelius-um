package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.ldap.OIDUser;

public interface OIDUserService
{
	List<OIDUser> search(String query, int page, int pageSize);
	Optional<OIDUser> getUser(String username);
	String getUserHomeArea(String username);
	void save(OIDUser user);
}
