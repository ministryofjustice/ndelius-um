package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.entry.RoleAssociationEntry;
import uk.co.bconline.ndelius.model.entry.RoleEntry;
import uk.co.bconline.ndelius.repository.ldap.RoleAssociationRepository;
import uk.co.bconline.ndelius.repository.ldap.RoleRepository;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.service.UserRoleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;
import static uk.co.bconline.ndelius.util.AuthUtils.myInteractions;
import static uk.co.bconline.ndelius.util.Constants.*;
import static uk.co.bconline.ndelius.util.LdapUtils.OBJECTCLASS;
import static uk.co.bconline.ndelius.util.NameUtils.join;

@Slf4j
@Service
public class UserRoleServiceImpl implements UserRoleService
{
	private final RoleService roleService;
	private final RoleRepository roleRepository;
	private final RoleAssociationRepository roleAssociationRepository;

	@Value("${spring.ldap.base}")
	private String ldapBase;

	@Value("${delius.ldap.base.users}")
	private String usersBase;

	@Value("${delius.ldap.base.roles}")
	private String rolesBase;

	@Autowired
	public UserRoleServiceImpl(
			RoleService roleService,
			RoleRepository roleRepository,
			RoleAssociationRepository roleAssociationRepository)
	{
		this.roleService = roleService;
		this.roleRepository = roleRepository;
		this.roleAssociationRepository = roleAssociationRepository;
	}

	@Override
	public Set<RoleEntry> getRolesICanAssign()
	{
		val myInteractions = myInteractions().collect(toSet());
		val privateAccess = myInteractions.contains(PRIVATE_ACCESS);
		val publicAccess = myInteractions.contains(PUBLIC_ACCESS);
		val nationalAccess = myInteractions.contains(NATIONAL_ACCESS);
		val localAccess = myInteractions.contains(LOCAL_ACCESS);
		val level1Access = myInteractions.contains(LEVEL1_ACCESS);
		val level2Access = myInteractions.contains(LEVEL2_ACCESS);
		val level3Access = myInteractions.contains(LEVEL3_ACCESS);

		return roleService.getAllRoles().stream()
				.filter(role -> (privateAccess || !"private".equalsIgnoreCase(role.getSector()))
						&& (publicAccess || !"public".equalsIgnoreCase(role.getSector()))
						&& (nationalAccess || !"national".equalsIgnoreCase(role.getAdminLevel()))
						&& (localAccess || !"local".equalsIgnoreCase(role.getAdminLevel()))
						&& (level1Access || !role.isLevel1())
						&& (level2Access || !role.isLevel2())
						&& (level3Access || !role.isLevel3()))
				.collect(toSet());
	}

	@Override
	public Set<RoleEntry> getUserRoles(String username)
	{
		val t = LocalDateTime.now();
		val r = stream(roleRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(join(",", "cn=" + username, usersBase))
                .where(OBJECTCLASS).is("NDRole")
                .or(OBJECTCLASS).is("NDRoleAssociation")).spliterator(), true)
				.map(role -> roleService.getRole(role.getName()).orElse(role))
				.collect(toSet());
		log.trace("--{}ms	LDAP lookup user roles", MILLIS.between(t, LocalDateTime.now()));
		return r;
	}

	@Override
	public Set<String> getUserInteractions(String username)
	{
		val t = LocalDateTime.now();
		val r = stream(roleRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(join(",", "cn=" + username, usersBase))
				.where("objectclass").isPresent()).spliterator(), true)
				.map(RoleEntry::getName)
				.map(roleService::getRole)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(RoleEntry::getInteractions)
				.flatMap(List::stream)
				.collect(toSet());
		log.trace("--{}ms	LDAP lookup user interactions", MILLIS.between(t, LocalDateTime.now()));
		return r;
	}

	@Override
	public void updateUserRoles(String username, Set<RoleEntry> roles)
	{
		log.debug("Deleting existing role associations");
		roleRepository.deleteAll(roleRepository.findAll(query()
				.searchScope(SearchScope.ONELEVEL)
				.base(join(",", "cn=" + username, usersBase))
				.where(OBJECTCLASS).is("NDRole")
				.or(OBJECTCLASS).is("NDRoleAssociation")));

		log.debug("Saving new role associations");
		ofNullable(roles).ifPresent(r ->
				roleAssociationRepository.saveAll(r.stream()
						.map(RoleEntry::getName)
						.map(name -> RoleAssociationEntry.builder()
								.name(name)
								.username(username)
								.aliasedObjectName(join(",", "cn=" + name, rolesBase, ldapBase))
								.build())
						.collect(toList())));
	}
}
