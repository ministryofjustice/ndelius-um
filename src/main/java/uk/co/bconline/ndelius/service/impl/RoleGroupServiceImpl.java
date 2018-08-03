package uk.co.bconline.ndelius.service.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.odm.annotations.Entry;
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

    public Iterable<RoleGroup> getRoleGroups()
    {
		val rolesICanAssign = stream(roleService.getRoles().spliterator(), false).map(Role::getName).collect(toSet());
        return stream(oidRoleGroupRepository
                .findAll(query()
                        .searchScope(ONELEVEL)
                        .base(OIDRoleGroup.class.getAnnotation(Entry.class).base())
                        .where("objectclass").like("*")).spliterator(), false)
				.peek(g -> g.setRoles(roleService
						.getRolesByParent(g.getName(), OIDRoleGroup.class).stream()
						.filter(role -> rolesICanAssign.contains(role.getName()))
						.collect(toList())))
				.filter(g -> !g.getRoles().isEmpty())
                .map(roleGroupTransformer::map)
                .collect(toList());
    }

    @Override
    public Optional<RoleGroup> getRoleGroup(String name)
	{
		val rolesICanAssign = stream(roleService.getRoles().spliterator(), false).map(Role::getName).collect(toSet());
        return oidRoleGroupRepository.findByName(name)
				.map(g -> {
					g.setRoles(roleService
							.getRolesByParent(name, OIDRoleGroup.class).stream()
							.filter(role -> rolesICanAssign.contains(role.getName()))
							.collect(toList()));
					return g;
				})
				.map(roleGroupTransformer::map);
    }
}
