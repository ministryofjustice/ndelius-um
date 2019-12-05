package uk.co.bconline.ndelius.service.impl;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.RoleGroup;
import uk.co.bconline.ndelius.model.entry.RoleEntry;
import uk.co.bconline.ndelius.service.RoleGroupService;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.service.UserRoleGroupService;
import uk.co.bconline.ndelius.service.UserRoleService;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class UserRoleGroupServiceImpl implements UserRoleGroupService
{
    private final RoleGroupService roleGroupService;
	private final UserRoleService userRoleService;
	private final RoleService roleService;

    @Autowired
    public UserRoleGroupServiceImpl(
			RoleGroupService roleGroupService,
			UserRoleService userRoleService,
			RoleService roleService) {
        this.roleGroupService = roleGroupService;
		this.userRoleService = userRoleService;
		this.roleService = roleService;
    }

    @Override
    public List<RoleGroup> getAssignableRoleGroups()
    {
		val rolesICanAssign = userRoleService.getRolesICanAssign().stream().map(RoleEntry::getName).collect(toSet());
        return roleGroupService.getRoleGroups().stream()
				.filter(g -> roleService.getRolesInGroup(g.getName()).stream()
						.anyMatch(role -> rolesICanAssign.contains(role.getName())))
                .collect(toList());
    }

    @Override
    public Optional<RoleGroup> getRoleGroup(String name)
	{
		val rolesICanAssign = userRoleService.getRolesICanAssign().stream().map(RoleEntry::getName).collect(toSet());
        return roleGroupService.getRoleGroup(name)
				.map(g -> {
					g.setRoles(g.getRoles().stream()
							.filter(role -> rolesICanAssign.contains(role.getName()))
							.collect(toList()));
					return g;
				});
    }
}
