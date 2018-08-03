package uk.co.bconline.ndelius.service.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.val;
import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.repository.oid.OIDRoleRepository;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.transformer.UserTransformer;

@Service
public class RoleServiceImpl implements RoleService
{
	private static final String ROLE_BASE = OIDRole.class.getAnnotation(Entry.class).base();
	private static final String PUBLIC_ACCESS = "UABI020";
	private static final String PRIVATE_ACCESS = "UABI021";
	private static final String LEVEL1_ACCESS = "UABI022";
	private static final String LEVEL2_ACCESS = "UABI023";
	private static final String LEVEL3_ACCESS = "UABI024";
	private static final String NATIONAL_ACCESS = "UABI025";
	private static final String LOCAL_ACCESS = "UABI026";

	private final OIDRoleRepository oidRoleRepository;
	private final UserTransformer userTransformer;

	@Autowired
	public RoleServiceImpl(
			OIDRoleRepository oidRoleRepository,
			UserTransformer userTransformer)
	{
		this.oidRoleRepository = oidRoleRepository;
		this.userTransformer = userTransformer;
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

		return getRolesByBase(ROLE_BASE)
				.filter(role -> (privateAccess || !"private".equalsIgnoreCase(role.getSector()))
						&& (publicAccess || !"public".equalsIgnoreCase(role.getSector()))
						&& (nationalAccess || !"national".equalsIgnoreCase(role.getAdminLevel()))
						&& (localAccess || !"local".equalsIgnoreCase(role.getAdminLevel()))
						&& (level1Access || !role.isLevel1())
						&& (level2Access || !role.isLevel2())
						&& (level3Access || !role.isLevel2()))
				.map(userTransformer::map)
				.collect(toList());
	}

	@Override
	public Optional<OIDRole> getOIDRole(String role)
	{
		return oidRoleRepository.findByName(role);
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
	 * Note: The directory structure for NDelius in OID is (see schema.ldif for example):
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
		return getRolesByParent(username, OIDUser.class)
				.map(OIDRole::getInteractions)
				.flatMap(List::stream)
				.collect(toList());
	}

	private Stream<OIDRole> getRolesByBase(String base)
	{
		return stream(oidRoleRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(base)
				.where("objectclass").like("NDRole*")).spliterator(), false);
	}
}
