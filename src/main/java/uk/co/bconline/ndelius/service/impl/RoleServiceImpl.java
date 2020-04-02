package uk.co.bconline.ndelius.service.impl;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.entry.RoleEntry;
import uk.co.bconline.ndelius.repository.ldap.RoleRepository;
import uk.co.bconline.ndelius.service.RoleService;

import java.util.Optional;
import java.util.Set;

import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;
import static uk.co.bconline.ndelius.util.LdapUtils.OBJECTCLASS;
import static uk.co.bconline.ndelius.util.NameUtils.join;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService
{
	private final RoleRepository roleRepository;

	@Value("${spring.ldap.base}")
	private String ldapBase;

	@Value("${delius.ldap.base.roles}")
	private String rolesBase;

	@Value("${delius.ldap.base.role-groups}")
	private String roleGroupsBase;

	@Autowired
	public RoleServiceImpl(
			RoleRepository roleRepository)
	{
		this.roleRepository = roleRepository;
	}

	@Override
	@Cacheable(value = "rolesets", key = "'all'")
	public Set<RoleEntry> getAllRoles()
	{
		return Sets.newHashSet(roleRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(rolesBase)
				.where(OBJECTCLASS).is("NDRole")
				.or(OBJECTCLASS).is("NDRoleAssociation")));
	}

	@Override
	@Cacheable(value = "roles")
	public Optional<RoleEntry> getRole(String role)
	{
		return roleRepository.findByName(role);
	}

	@Override
	@Cacheable(value = "rolesets")
	public Set<RoleEntry> getRolesInGroup(String group)
	{
		return Sets.newHashSet(roleRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(join(",", "cn=" + group, roleGroupsBase))
				.where(OBJECTCLASS).is("NDRole")
				.or(OBJECTCLASS).is("NDRoleAssociation")));
	}
}
