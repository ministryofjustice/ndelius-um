package uk.co.bconline.ndelius.service;

import java.util.Optional;
import java.util.Set;

import uk.co.bconline.ndelius.model.ldap.OIDRole;

public interface RoleService
{
	Set<OIDRole> getAllRoles();
	Optional<OIDRole> getRole(String role);
	Set<OIDRole> getRolesInGroup(String group);
}
