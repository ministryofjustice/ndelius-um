package uk.co.bconline.ndelius.service.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.val;
import uk.co.bconline.ndelius.model.RoleGroup;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.repository.oid.OIDRoleGroupRepository;
import uk.co.bconline.ndelius.service.RoleGroupService;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.service.UserRoleService;
import uk.co.bconline.ndelius.transformer.RoleGroupTransformer;

@Service
public class RoleGroupServiceImpl implements RoleGroupService
{
    private final OIDRoleGroupRepository oidRoleGroupRepository;
    private final RoleGroupTransformer roleGroupTransformer;
	private final UserRoleService userRoleService;
	private final RoleService roleService;

    @Autowired
    public RoleGroupServiceImpl(
    		OIDRoleGroupRepository oidRoleGroupRepository,
			RoleGroupTransformer roleGroupTransformer,
			UserRoleService userRoleService,
			RoleService roleService){
        this.oidRoleGroupRepository = oidRoleGroupRepository;
        this.roleGroupTransformer = roleGroupTransformer;
		this.userRoleService = userRoleService;
		this.roleService = roleService;
    }

    @Override
	@Cacheable(value = "roleGroups", key = "'all'")
    public Iterable<RoleGroup> getRoleGroups()
    {
		val rolesICanAssign = userRoleService.getRolesICanAssign().stream().map(OIDRole::getName).collect(toSet());
        return stream(oidRoleGroupRepository.findAll().spliterator(), false)
				.filter(g -> roleService.getRolesInGroup(g.getName()).stream()
						.anyMatch(role -> rolesICanAssign.contains(role.getName())))
                .map(roleGroupTransformer::map)
                .collect(toList());
    }

    @Override
	@Cacheable(value = "roleGroups")
    public Optional<RoleGroup> getRoleGroup(String name)
	{
		val rolesICanAssign = userRoleService.getRolesICanAssign().stream().map(OIDRole::getName).collect(toSet());
        return oidRoleGroupRepository.findByName(name)
				.map(g -> {
					g.setRoles(roleService.getRolesInGroup(g.getName()).stream()
							.filter(role -> rolesICanAssign.contains(role.getName()))
							.collect(toSet()));
					return g;
				})
				.map(roleGroupTransformer::map);
    }
}
