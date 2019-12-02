package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.entry.RoleEntry;

import java.util.Set;

public interface UserRoleService
{
	Set<RoleEntry> getRolesICanAssign();
	Set<RoleEntry> getUserRoles(String username);
	Set<String> getUserInteractions(String username);
	void updateUserRoles(String username, Set<RoleEntry> roles);
}
