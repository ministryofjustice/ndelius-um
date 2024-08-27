package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.entry.RoleEntry;

import java.util.List;
import java.util.Set;

public interface UserRoleService
{
	Set<RoleEntry> getRolesICanAssign();
	Set<RoleEntry> getUserRoles(String username);

	Set<String> getUserRoleNames(String username);
	List<String> getAllUsersWithRole(String role);
	Set<RoleEntry> getClientRoles(String clientId);
	Set<String> getUserInteractions(String username);
	void updateUserRoles(String username, Set<RoleEntry> roles);
}
