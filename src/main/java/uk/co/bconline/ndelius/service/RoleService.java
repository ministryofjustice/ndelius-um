package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.ldap.OIDRole;

public interface RoleService
{
	Iterable<Role> getRoles();
	Optional<OIDRole> getOIDRole(String role);
	List<OIDRole> getRolesByParent(String parent, Class<?> parentClass);
}
