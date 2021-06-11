package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.entry.RoleAssociationEntry;
import uk.co.bconline.ndelius.model.entry.RoleEntry;

import javax.naming.Name;
import java.util.Optional;
import java.util.Set;

public interface RoleService
{
	Set<RoleEntry> getAllRoles();
	Set<RoleEntry> getRolesInGroup(String group);
	Optional<RoleEntry> getRole(Name id);
	Optional<RoleEntry> dereference(RoleAssociationEntry association);
}
