package uk.co.bconline.ndelius.service.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.val;
import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.RoleGroup;
import uk.co.bconline.ndelius.model.ldap.OIDRoleGroup;
import uk.co.bconline.ndelius.repository.oid.OIDRoleGroupRepository;
import uk.co.bconline.ndelius.service.RoleGroupService;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.transformer.RoleGroupTransformer;

@Service
public class RoleGroupServiceImpl implements RoleGroupService
{
    private final OIDRoleGroupRepository oidRoleGroupRepository;
    private final RoleGroupTransformer roleGroupTransformer;
    private final RoleService roleService;

    @Autowired
    public RoleGroupServiceImpl(OIDRoleGroupRepository oidRoleGroupRepository, RoleGroupTransformer roleGroupTransformer, RoleService roleService){
        this.oidRoleGroupRepository = oidRoleGroupRepository;
        this.roleGroupTransformer = roleGroupTransformer;
        this.roleService = roleService;
    }

    @Override
	@Cacheable(value = "roleGroups", key = "'all'")
    public Iterable<RoleGroup> getRoleGroups()
    {
		val rolesICanAssign = roleService.getRoles().stream().map(Role::getName).collect(toSet());
        return stream(oidRoleGroupRepository.findAll().spliterator(), false)
				.filter(g -> roleService
						.getRolesByParent(g.getName(), OIDRoleGroup.class)
						.anyMatch(role -> rolesICanAssign.contains(role.getName())))
                .map(roleGroupTransformer::map)
                .collect(toList());
    }

    @Override
	@Cacheable(value = "roleGroupsByName")
    public Optional<RoleGroup> getRoleGroup(String name)
	{
		val rolesICanAssign = roleService.getRoles().stream().map(Role::getName).collect(toSet());
        return oidRoleGroupRepository.findByName(name)
				.map(g -> {
					g.setRoles(roleService
							.getRolesByParent(name, OIDRoleGroup.class)
							.filter(role -> rolesICanAssign.contains(role.getName()))
							.collect(toList()));
					return g;
				})
				.map(roleGroupTransformer::map);
    }
}
