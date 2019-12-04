package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.entry.RoleEntry;

import java.util.Optional;
import java.util.Set;

public interface RoleService
{
	Set<RoleEntry> getAllRoles();
	Optional<RoleEntry> getRole(String role);
	Set<RoleEntry> getRolesInGroup(String group);
}
