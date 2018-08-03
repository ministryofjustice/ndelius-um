package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.ldap.OIDRole;

public interface RoleService
{
	List<Role> getRoles();
	Optional<OIDRole> getOIDRole(String role);
	Stream<OIDRole> getRolesByParent(String parent, Class<?> parentClass);
	List<String> getUserInteractions(String username);
}
