package uk.co.bconline.ndelius.service.impl;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Optionals;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.entry.RoleAssociationEntry;
import uk.co.bconline.ndelius.model.entry.RoleEntry;
import uk.co.bconline.ndelius.repository.ldap.RoleAssociationRepository;
import uk.co.bconline.ndelius.repository.ldap.RoleRepository;
import uk.co.bconline.ndelius.service.RoleService;

import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;
import static uk.co.bconline.ndelius.util.LdapUtils.OBJECTCLASS;
import static uk.co.bconline.ndelius.util.NameUtils.join;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService
{
	private final RoleRepository roleRepository;
	private final RoleAssociationRepository roleAssociationRepository;

	@Value("${spring.ldap.base}")
	private String ldapBase;

	@Value("${delius.ldap.base.roles}")
	private String rolesBase;

	@Value("${delius.ldap.base.role-groups}")
	private String roleGroupsBase;

	@Autowired
	public RoleServiceImpl(RoleRepository roleRepository,
						   RoleAssociationRepository roleAssociationRepository)
	{
		this.roleRepository = roleRepository;
		this.roleAssociationRepository = roleAssociationRepository;
	}

	@Override
	@Cacheable(value = "rolesets", key = "'all'")
	public Set<RoleEntry> getAllRoles()
	{
		return Sets.newHashSet(roleRepository.findAll());
	}

	@Override
	@Cacheable(value = "roles")
	public Optional<RoleEntry> getRole(String role)
	{
		return roleRepository.findByName(role);
	}

	/**
	 * Efficiently de-references the roles within a group, making use of parallel streams and caching.
	 *
	 * @param group Name of the group.
	 * @return A collection containing the Role entries in the group.
	 */
	@Override
	@Cacheable(value = "rolesets")
	public Set<RoleEntry> getRolesInGroup(String group)
	{
		return stream(roleAssociationRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(join(",", "cn=" + group, roleGroupsBase))
				.where(OBJECTCLASS).is("NDRoleAssociation")).spliterator(), true)
				.map(this::dereference)
				.flatMap(Optionals::toStream)
				.collect(toSet());
	}

	@Override
	public Optional<RoleEntry> dereference(RoleAssociationEntry association) {
		val base = LdapUtils.newLdapName(ldapBase);
		val aliasDn = LdapUtils.newLdapName(association.getAliasedObjectName());
		val aliasId = LdapUtils.removeFirst(aliasDn, base);
		return roleRepository.findById(aliasId);
	}
}
