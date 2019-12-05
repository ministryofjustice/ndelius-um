package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.RoleGroup;
import uk.co.bconline.ndelius.model.entry.RoleEntry;
import uk.co.bconline.ndelius.repository.ldap.RoleGroupRepository;
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
    private final RoleGroupRepository roleGroupRepository;
    private final RoleGroupTransformer roleGroupTransformer;
	private final RoleService roleService;

    @Autowired
    public RoleGroupServiceImpl(
    		RoleGroupRepository roleGroupRepository,
			RoleGroupTransformer roleGroupTransformer,
			RoleService roleService){
        this.roleGroupRepository = roleGroupRepository;
        this.roleGroupTransformer = roleGroupTransformer;
		this.roleService = roleService;
    }

    @Override
	@Cacheable(value = "roleGroups", key = "'all'")
    public List<RoleGroup> getRoleGroups()
    {
        return stream(roleGroupRepository.findAll().spliterator(), true)
                .map(roleGroupTransformer::map)
                .collect(toList());
    }

    @Override
	@Cacheable(value = "roleGroups")
    public Optional<RoleGroup> getRoleGroup(String name)
	{
        return roleGroupRepository.findByName(name)
				.map(g -> {
					g.setRoles(roleService.getRolesInGroup(g.getName()).parallelStream()
							.map(RoleEntry::getName)
							.map(roleService::getRole)
							.filter(Optional::isPresent).map(Optional::get)
							.collect(toSet()));
					return g;
				})
				.map(roleGroupTransformer::map);
    }
}
