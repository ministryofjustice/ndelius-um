package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.entry.RoleAssociationEntry;
import uk.co.bconline.ndelius.model.entry.RoleEntry;

import java.util.Optional;
import java.util.Set;

public interface RoleService
{
	Set<RoleEntry> getAllRoles();
	Set<RoleEntry> getRolesInGroup(String group);
	Optional<RoleEntry> getRole(String role);
	Optional<RoleEntry> dereference(RoleAssociationEntry association);
}
