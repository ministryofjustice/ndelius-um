package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.query.SearchScope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.model.ldap.OIDRoleAssociation;
import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.repository.oid.OIDRoleAssociationRepository;
import uk.co.bconline.ndelius.repository.oid.OIDRoleRepository;
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
import static uk.co.bconline.ndelius.util.LdapUtils.OBJECTCLASS;
import static uk.co.bconline.ndelius.util.NameUtils.join;

@Slf4j
@Service
public class UserRoleServiceImpl implements UserRoleService
{
	private static final String ROLE_BASE = OIDRole.class.getAnnotation(Entry.class).base();
	private static final String USER_BASE = OIDUser.class.getAnnotation(Entry.class).base();
	private static final String PUBLIC_ACCESS = "UABI020";
	private static final String PRIVATE_ACCESS = "UABI021";
	private static final String LEVEL1_ACCESS = "UABI022";
	private static final String LEVEL2_ACCESS = "UABI023";
	private static final String LEVEL3_ACCESS = "UABI024";
	private static final String NATIONAL_ACCESS = "UABI025";
	private static final String LOCAL_ACCESS = "UABI026";

	private final RoleService roleService;
	private final OIDRoleRepository roleRepository;
	private final OIDRoleAssociationRepository roleAssociationRepository;

	@Value("${oid.base}")
	private String oidBase;

	@Autowired
	public UserRoleServiceImpl(
			RoleService roleService,
			OIDRoleRepository roleRepository,
			OIDRoleAssociationRepository roleAssociationRepository)
	{
		this.roleService = roleService;
		this.roleRepository = roleRepository;
		this.roleAssociationRepository = roleAssociationRepository;
	}

	@Override
	public Set<OIDRole> getRolesICanAssign()
	{
		val myInteractions = SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(toSet());
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
						&& (level3Access || !role.isLevel2()))
				.collect(toSet());
	}

	@Override
	public Set<OIDRole> getUserRoles(String username)
	{
		val t = LocalDateTime.now();
		val r = stream(roleRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(join(",", "cn=" + username, USER_BASE))
                .where(OBJECTCLASS).is("NDRole")
                .or(OBJECTCLASS).is("NDRoleAssociation")).spliterator(), true)
				.map(role -> role.getName().startsWith("UMBT") || role.getName().startsWith("UABT")?
								roleService.getRole(role.getName()).orElse(role): role)
				.collect(toSet());
		log.trace("--{}ms	OID lookup user roles", MILLIS.between(t, LocalDateTime.now()));
		return r;
	}

	@Override
	public Set<String> getUserInteractions(String username)
	{
		val t = LocalDateTime.now();
		val r = stream(roleRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(join(",", "cn=" + username, USER_BASE))
				.where("cn").like("UMBT*")
				.or("cn").like("UABT*")).spliterator(), true)
				.map(OIDRole::getName)
				.map(roleService::getRole)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(OIDRole::getInteractions)
				.flatMap(List::stream)
				.collect(toSet());
		log.trace("--{}ms	OID lookup user interactions", MILLIS.between(t, LocalDateTime.now()));
		return r;
	}

	@Override
	public void updateUserRoles(String username, Set<OIDRole> roles)
	{
		log.debug("Deleting existing role associations");
		roleRepository.deleteAll(roleRepository.findAll(query()
				.searchScope(SearchScope.ONELEVEL)
				.base(join(",", "cn=" + username, USER_BASE))
				.where(OBJECTCLASS).is("NDRole")
				.or(OBJECTCLASS).is("NDRoleAssociation")));

		log.debug("Saving new role associations");
		ofNullable(roles).ifPresent(r ->
				roleAssociationRepository.saveAll(r.stream()
						.map(OIDRole::getName)
						.map(name -> OIDRoleAssociation.builder()
								.name(name)
								.username(username)
								.aliasedObjectName(join(",", "cn=" + name, ROLE_BASE, oidBase))
								.build())
						.collect(toList())));
	}
}
