package uk.co.bconline.ndelius.service;

import java.util.Set;

import uk.co.bconline.ndelius.model.ldap.OIDRole;

public interface UserRoleService
{
	Set<OIDRole> getRolesICanAssign();
	Set<OIDRole> getUserRoles(String username);
	Set<String> getUserInteractions(String username);
	void updateUserRoles(String username, Set<OIDRole> roles);
}
