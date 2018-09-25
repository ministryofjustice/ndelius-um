package uk.co.bconline.ndelius.service.impl;

import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.model.ldap.OIDRoleGroup;
import uk.co.bconline.ndelius.repository.oid.OIDRoleRepository;
import uk.co.bconline.ndelius.service.RoleService;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService
{
	private static final String ROLE_BASE = OIDRole.class.getAnnotation(Entry.class).base();
	private static final String GROUP_BASE = OIDRoleGroup.class.getAnnotation(Entry.class).base();

	private final OIDRoleRepository roleRepository;

	@Value("${oid.base}")
	private String oidBase;

	@Autowired
	public RoleServiceImpl(
			OIDRoleRepository roleRepository)
	{
		this.roleRepository = roleRepository;
	}

	@Override
	@Cacheable(value = "roles", key = "'all'")
	public Set<OIDRole> getAllRoles()
	{
		return Sets.newHashSet(roleRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(ROLE_BASE)
				.where("objectclass").like("NDRole*")));
	}

	@Override
	@Cacheable(value = "roles")
	public Optional<OIDRole> getRole(String role)
	{
		return roleRepository.findByName(role);
	}

	@Override
	@Cacheable(value = "roles")
	public Set<OIDRole> getRolesInGroup(String group)
	{
		return Sets.newHashSet(roleRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(String.format("cn=%s,%s", group, GROUP_BASE))
				.where("objectclass").like("NDRole*")));
	}
}
