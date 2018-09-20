package uk.co.bconline.ndelius.service.impl;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.query.SearchScope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.model.ldap.OIDRoleAssociation;
import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.repository.oid.OIDRoleAssociationRepository;
import uk.co.bconline.ndelius.repository.oid.OIDRoleRepository;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.transformer.RoleTransformer;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService
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

	private final OIDRoleRepository roleRepository;
	private final OIDRoleAssociationRepository roleAssociationRepository;
	private final RoleTransformer roleTransformer;

	@Value("${oid.base}")
	private String oidBase;

	@Autowired
	public RoleServiceImpl(
			OIDRoleRepository roleRepository,
			OIDRoleAssociationRepository roleAssociationRepository,
			RoleTransformer roleTransformer)
	{
		this.roleRepository = roleRepository;
		this.roleAssociationRepository = roleAssociationRepository;
		this.roleTransformer = roleTransformer;
	}

	@Override
	public List<Role> getRoles()
	{
		val me = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		val myInteractions = getUserInteractions(me);
		val privateAccess = myInteractions.contains(PRIVATE_ACCESS);
		val publicAccess = myInteractions.contains(PUBLIC_ACCESS);
		val nationalAccess = myInteractions.contains(NATIONAL_ACCESS);
		val localAccess = myInteractions.contains(LOCAL_ACCESS);
		val level1Access = myInteractions.contains(LEVEL1_ACCESS);
		val level2Access = myInteractions.contains(LEVEL2_ACCESS);
		val level3Access = myInteractions.contains(LEVEL3_ACCESS);

		return getUnfilteredRoles()
				.filter(role -> (privateAccess || !"private".equalsIgnoreCase(role.getSector()))
						&& (publicAccess || !"public".equalsIgnoreCase(role.getSector()))
						&& (nationalAccess || !"national".equalsIgnoreCase(role.getAdminLevel()))
						&& (localAccess || !"local".equalsIgnoreCase(role.getAdminLevel()))
						&& (level1Access || !role.isLevel1())
						&& (level2Access || !role.isLevel2())
						&& (level3Access || !role.isLevel2()))
				.map(roleTransformer::map)
				.collect(toList());
	}

	@Override
	public Stream<OIDRole> getUnfilteredRoles()
	{
		return getRolesByBase(ROLE_BASE);
	}

	@Override
	public Optional<OIDRole> getOIDRole(String role)
	{
		return roleRepository.findByName(role);
	}

	@Override
	public Stream<OIDRole> getRolesByParent(String parent, Class<?> parentClass)
	{
		return getRolesByBase(String.format("cn=%s,%s", parent, parentClass.getAnnotation(Entry.class).base()))
				.map(role -> getOIDRole(role.getName()).orElse(role));
	}

	/**
	 * Return a list of business interactions for a given user.
	 *
	 *
	 * In NDelius, the interactions/roles are stored as uibusinessinteraction attributes on NDRole objects within the
	 * ndRoleCatalogue. These are then aliased as sub-entries for each user to assign a set of allowed interactions.
	 *
	 * Note: The directory structure for NDelius in OID is (see oid.ldif for example):
	 * dc=...
	 * 	cn=Users
	 * 		cn=ndRoleCatalogue
	 * 			cn=XXBT001
	 * 			objectclass: NDRole
	 * 			uibusinessinteraction: XXBI001
	 * 			uibusinessinteraction: XXBI002
	 * 			...
	 * 		cn=user1
	 * 			cn=XXBT001
	 * 			objectclass: alias
	 * 			aliasedObjectName: cn=XXBT001,cn=ndRoleCatalogue,cn=Users,dc=...
	 * 			...
	 * 		cn=user2
	 * 			...
	 *
	 * @param username The cn of the user to retrieve the roles for
	 * @return A list of roles eg. [XXBI001, XXBI002]
	 */
	@Override
	public List<String> getUserInteractions(String username)
	{
		return getRolesByBase(String.format("cn=%s,%s", username, USER_BASE))
				.filter(role -> role.getName().startsWith("UMBT") || role.getName().startsWith("UABT"))
				.map(OIDRole::getName)
				.map(this::getOIDRole)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(OIDRole::getInteractions)
				.flatMap(List::stream)
				.collect(toList());
	}

	@Override
	public void updateUserRoles(String username, List<OIDRole> roles)
	{
		log.debug("Deleting existing role associations");
		roleRepository.deleteAll(roleRepository.findAll(query()
				.searchScope(SearchScope.ONELEVEL)
				.base(String.format("cn=%s,%s", username, USER_BASE))
				.where("objectClass").like("NDRole*")));

		log.debug("Saving new role associations");
		ofNullable(roles).ifPresent(r ->
				roleAssociationRepository.saveAll(r.stream()
						.map(OIDRole::getName)
						.map(name -> OIDRoleAssociation.builder()
								.name(name)
								.username(username)
								.aliasedObjectName(String.format("cn=%s,%s,%s", name, ROLE_BASE, oidBase))
								.build())
						.collect(toList())));
	}

	private Stream<OIDRole> getRolesByBase(String base)
	{
		return stream(roleRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(base)
				.where("objectclass").like("NDRole*")).spliterator(), false);
	}
}
