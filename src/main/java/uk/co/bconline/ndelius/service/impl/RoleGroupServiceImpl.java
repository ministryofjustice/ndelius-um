package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.RoleGroup;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.repository.oid.OIDRoleGroupRepository;
import uk.co.bconline.ndelius.service.RoleGroupService;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.transformer.RoleGroupTransformer;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

@Service
public class RoleGroupServiceImpl implements RoleGroupService
{
    private final OIDRoleGroupRepository oidRoleGroupRepository;
    private final RoleGroupTransformer roleGroupTransformer;
	private final RoleService roleService;

    @Autowired
    public RoleGroupServiceImpl(
    		OIDRoleGroupRepository oidRoleGroupRepository,
			RoleGroupTransformer roleGroupTransformer,
			RoleService roleService){
        this.oidRoleGroupRepository = oidRoleGroupRepository;
        this.roleGroupTransformer = roleGroupTransformer;
		this.roleService = roleService;
    }

    @Override
	@Cacheable(value = "roleGroups", key = "'all'")
    public List<RoleGroup> getRoleGroups()
    {
        return stream(oidRoleGroupRepository.findAll().spliterator(), true)
                .map(roleGroupTransformer::map)
                .collect(toList());
    }

    @Override
	@Cacheable(value = "roleGroups")
    public Optional<RoleGroup> getRoleGroup(String name)
	{
        return oidRoleGroupRepository.findByName(name)
				.map(g -> {
					g.setRoles(roleService.getRolesInGroup(g.getName()).parallelStream()
							.map(OIDRole::getName)
							.map(roleService::getRole)
							.filter(Optional::isPresent).map(Optional::get)
							.collect(toSet()));
					return g;
				})
				.map(roleGroupTransformer::map);
    }
}
