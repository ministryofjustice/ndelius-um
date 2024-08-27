package uk.co.bconline.ndelius.service.impl;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Optionals;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.entry.RoleAssociationEntry;
import uk.co.bconline.ndelius.model.entry.RoleEntry;
import uk.co.bconline.ndelius.repository.ldap.RoleAssociationRepository;
import uk.co.bconline.ndelius.repository.ldap.RoleRepository;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.service.UserRoleService;
import uk.co.bconline.ndelius.transformer.RoleTransformer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;
import static org.springframework.ldap.query.SearchScope.SUBTREE;
import static uk.co.bconline.ndelius.util.AuthUtils.myInteractions;
import static uk.co.bconline.ndelius.util.Constants.*;
import static uk.co.bconline.ndelius.util.LdapUtils.OBJECTCLASS;
import static uk.co.bconline.ndelius.util.NameUtils.join;

@Slf4j
@Service
public class UserRoleServiceImpl implements UserRoleService {

	private final RoleService roleService;
	private final RoleRepository roleRepository;
	private final RoleAssociationRepository roleAssociationRepository;
	private final RoleTransformer roleTransformer;

	@Value("${delius.ldap.base.users}")
	private String usersBase;

	@Value("${delius.ldap.base.clients}")
	private String clientsBase;

	@Autowired
	public UserRoleServiceImpl(
			RoleService roleService,
			RoleRepository roleRepository,
			RoleAssociationRepository roleAssociationRepository,
			RoleTransformer roleTransformer) {
		this.roleService = roleService;
		this.roleRepository = roleRepository;
		this.roleAssociationRepository = roleAssociationRepository;
		this.roleTransformer = roleTransformer;
	}

	@Override
	public Set<RoleEntry> getRolesICanAssign() {
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
	public Set<RoleEntry> getUserRoles(String username) {
		return getAssignedRoles(username, usersBase);
	}

	@Override
	public Set<String> getUserRoleNames(String username) {
		return stream(getAssignedRoleAssociations(username, usersBase).spliterator(), false)
				.map(RoleAssociationEntry::getCn)
				.collect(toSet());
	}

	@Override
	public Set<RoleEntry> getClientRoles(String clientId) {
		return getAssignedRoles(clientId, clientsBase);
	}

	private Set<RoleEntry> getAssignedRoles(String id, String base) {
		val t = LocalDateTime.now();
		val r = stream(getAssignedRoleAssociations(id, base).spliterator(), true)
				.map(roleService::dereference)
				.flatMap(Optionals::toStream)
				.collect(toSet());
		log.trace("--{}ms	LDAP lookup user roles", MILLIS.between(t, LocalDateTime.now()));
		return r;
	}

	private Iterable<RoleAssociationEntry> getAssignedRoleAssociations(String id, String base) {
		return roleAssociationRepository.findAll(query()
				.searchScope(ONELEVEL)
				.base(join(",", "cn=" + id, base))
				.where(OBJECTCLASS).is("NDRoleAssociation"));
	}

	@Override
	public Set<String> getUserInteractions(String username) {
		val t = LocalDateTime.now();
		val r = getUserRoles(username).stream()
				.map(RoleEntry::getInteractions)
				.flatMap(List::stream)
				.collect(toSet());
		log.trace("--{}ms	LDAP lookup user interactions", MILLIS.between(t, LocalDateTime.now()));
		return r;
	}

	@Override
	public void updateUserRoles(String username, Set<RoleEntry> roles) {
		log.debug("Deleting any invalid role associations (non-aliases)");
		roleRepository.deleteAll(roleRepository.findAll(query()
				.searchScope(SearchScope.ONELEVEL)
				.base(join(",", "cn=" + username, usersBase))
				.where(OBJECTCLASS).is("NDRole")));

		log.debug("Fetching existing role associations");
		val existingRoles = stream(getAssignedRoleAssociations(username, usersBase).spliterator(), true)
				.map(RoleAssociationEntry::getCn)
				.collect(toSet());
		val newRoles = ofNullable(roles).map(r -> r.stream()
				.map(RoleEntry::getName)
				.collect(toSet())).orElse(emptySet());
		val rolesToAdd = Sets.difference(newRoles, existingRoles);
		val rolesToRemove = Sets.difference(existingRoles, newRoles);

		log.debug("Removing {} role association(s)", rolesToRemove.size());
		rolesToRemove.parallelStream()
				.map(role -> roleTransformer.buildAssociation(username, role))
				.forEach(roleAssociationRepository::delete);

		log.debug("Adding {} role association(s)", rolesToAdd.size());
		rolesToAdd.parallelStream()
				.map(role -> roleTransformer.buildAssociation(username, role))
				.forEach(roleAssociationRepository::save);
	}

	@Override
	public List<String> getAllUsersWithRole(String role)
	{
		if (role == null || role.isBlank()) return emptyList();

		return stream(roleAssociationRepository.findAll(query()
				.searchScope(SUBTREE)
				.base(usersBase)
				.where(OBJECTCLASS).is("NDRoleAssociation").and("cn").like(role)).spliterator(), false)
				.map(user -> LdapUtils.getStringValue(user.getDn(), user.getDn().size() - 2).toLowerCase()) // username is 2nd-to-last part of distinguished name
				.collect(toList());
	}
}
