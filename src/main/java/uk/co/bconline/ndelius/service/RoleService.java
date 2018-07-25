package uk.co.bconline.ndelius.service;

import java.util.List;

import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.ldap.OIDRole;

public interface RoleService
{
	Iterable<Role> getRoles();

    List<OIDRole> getRolesByParent(String parent, Class<?> parentClass);
}
