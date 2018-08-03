package uk.co.bconline.ndelius.service.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.query.SearchScope;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.model.ldap.OIDRoleAssociation;
import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.model.ldap.OIDUserAlias;
import uk.co.bconline.ndelius.model.ldap.projections.OIDUserHomeArea;
import uk.co.bconline.ndelius.repository.oid.OIDRoleAssociationRepository;
import uk.co.bconline.ndelius.repository.oid.OIDUserAliasRepository;
import uk.co.bconline.ndelius.repository.oid.OIDUserRepository;
import uk.co.bconline.ndelius.service.OIDUserService;
import uk.co.bconline.ndelius.service.RoleService;

@Slf4j
@Service
public class OIDUserDetailsService implements OIDUserService, UserDetailsService
{
	private static final String USER_BASE = OIDUser.class.getAnnotation(Entry.class).base();

	@Value("${oid.base}")
	private String oidBase;

	private final OIDUserRepository userRepository;
	private final OIDUserAliasRepository userAliasRepository;
	private final OIDRoleAssociationRepository roleAssociationRepository;
	private final RoleService roleService;

	@Autowired
	public OIDUserDetailsService(
			OIDUserRepository userRepository,
			OIDUserAliasRepository userAliasRepository,
			OIDRoleAssociationRepository roleAssociationRepository,
			RoleService roleService)
	{
		this.userRepository = userRepository;
		this.userAliasRepository = userAliasRepository;
		this.roleAssociationRepository = roleAssociationRepository;
		this.roleService = roleService;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
	{
		return userRepository
				.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)));
	}

	/**
	 * Search for a list of users with a single text query.
	 *
	 * The search query will be tokenized on space, then each token will be AND matched with wildcards. If any token
	 * is a single character, it will be treated as an initial on givenName.
	 *
	 * eg.
	 *
	 * "john"		-> (|(givenName=*john*)(sn=*john*)(cn=*john*))
	 * "john smith"	-> (&(|(givenName=*john*)(sn=*john*)(cn=*john*))(|(givenName=*smith*)(sn=*smith*)(cn=*smith*)))
	 * "J Bloggs"	-> (&(givenName=J*)(|(givenName=*Bloggs*)(sn=*Bloggs*)(cn=*Bloggs*)))
	 *
	 * @param query space-delimited query string
	 * @param page 1-based index of page to return
	 * @param pageSize number of results per page to return
	 * @return a set of matching users from OID
	 */
	@Override
	public List<OIDUser> search(String query, int page, int pageSize)
	{
		Filter filter = Stream.of(query.split(" "))
				.map(token -> token.length() > 1?
						query().where("givenName").whitespaceWildcardsLike(token)
								.or("sn").whitespaceWildcardsLike(token)
								.or("cn").whitespaceWildcardsLike(token):
						query().where("givenName").like(token + "*"))
				.collect(AndFilter::new, (f, q) -> f.and(q.filter()), AndFilter::and);

		log.debug("Searching OID: {}", filter.encode());

		return stream(userRepository
				.findAll(query()
						.base(USER_BASE)
						.countLimit(pageSize * page)
						.filter(filter))
				.spliterator(), false)
				.skip((long) pageSize * (page-1))
				.collect(toList());
	}

	@Override
	public Optional<OIDUser> getUser(String username)
	{
		Optional<OIDUser> user = userRepository.findByUsername(username);
		return user.map(u -> u.toBuilder()
				.roles(roleService.getRolesByParent(username, OIDUser.class))
				.aliasUsername(userAliasRepository.findByAliasedUserDn(u.getDn().toString() + "," + oidBase)
						.map(OIDUserAlias::getUsername).orElse(username))
				.build());
	}

	@Override
	public String getUserHomeArea(String username)
	{
		return userRepository.getOIDUserHomeAreaByUsername(username).map(OIDUserHomeArea::getHomeArea).orElse(null);
	}

	@Override
	public void save(OIDUser user)
	{
		// Save user
		userRepository.save(user);

		// User alias
		userAliasRepository.findByAliasedUserDn(String.format("cn=%s,%s,%s", user.getUsername(), USER_BASE, oidBase))
				.ifPresent(userAliasRepository::delete);
		if (user.getAliasUsername() != null && !user.getAliasUsername().equals(user.getUsername()))
		{
			userAliasRepository.save(OIDUserAlias.builder()
					.username(user.getAliasUsername())
					.aliasedUserDn(user.getDn().toString() + "," + oidBase)
					.build());
		}

		// Role associations
		roleAssociationRepository.deleteAll(roleAssociationRepository.findAll(query()
				.searchScope(SearchScope.ONELEVEL)
				.base(String.format("cn=%s,%s", user.getUsername(), USER_BASE))
				.where("objectclass").is("alias")));
		roleAssociationRepository.saveAll(user.getRoles().stream()
				.map(OIDRole::getName)
				.map(name -> OIDRoleAssociation.builder()
						.name(name)
						.username(user.getUsername())
						.aliasedObjectName(String.format("cn=%s,%s,%s", name,
								OIDRole.class.getAnnotation(Entry.class).base(), oidBase))
						.build())
				.collect(toList()));
	}
}
