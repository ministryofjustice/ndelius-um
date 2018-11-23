package uk.co.bconline.ndelius.service.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.val;
import uk.co.bconline.ndelius.model.RoleGroup;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.service.RoleGroupService;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.service.UserRoleGroupService;
import uk.co.bconline.ndelius.service.UserRoleService;

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
		val rolesICanAssign = userRoleService.getRolesICanAssign().stream().map(OIDRole::getName).collect(toSet());
        return roleGroupService.getRoleGroups().stream()
				.filter(g -> roleService.getRolesInGroup(g.getName()).stream()
						.anyMatch(role -> rolesICanAssign.contains(role.getName())))
                .collect(toList());
    }

    @Override
    public Optional<RoleGroup> getRoleGroup(String name)
	{
		val rolesICanAssign = userRoleService.getRolesICanAssign().stream().map(OIDRole::getName).collect(toSet());
        return roleGroupService.getRoleGroup(name)
				.map(g -> {
					g.setRoles(g.getRoles().stream()
							.filter(role -> rolesICanAssign.contains(role.getName()))
							.collect(toList()));
					return g;
				});
    }
}
