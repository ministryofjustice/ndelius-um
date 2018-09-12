package uk.co.bconline.ndelius.service.impl;

import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.query.SearchScope;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.ldap.*;
import uk.co.bconline.ndelius.model.ldap.projections.OIDUserHomeArea;
import uk.co.bconline.ndelius.repository.oid.OIDRoleAssociationRepository;
import uk.co.bconline.ndelius.repository.oid.OIDUserAliasRepository;
import uk.co.bconline.ndelius.repository.oid.OIDUserPreferencesRepository;
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
	private final OIDUserPreferencesRepository preferencesRepository;
	private final RoleService roleService;

	@Autowired
	public OIDUserDetailsService(
			OIDUserRepository userRepository,
			OIDUserAliasRepository userAliasRepository,
			OIDRoleAssociationRepository roleAssociationRepository,
			OIDUserPreferencesRepository preferencesRepository,
			RoleService roleService)
	{
		this.userRepository = userRepository;
		this.userAliasRepository = userAliasRepository;
		this.roleAssociationRepository = roleAssociationRepository;
		this.preferencesRepository = preferencesRepository;
		this.roleService = roleService;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
	{
		return getBasicUser(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)));
	}

	/**
	 * Search for a list of users with a single text query.
	 *
	 * The search query will be tokenized on space, then each token will be AND matched with wildcards.
	 *
	 * eg.
	 *
	 * "john"		-> (|(givenName=*john*)(sn=*john*)(cn=*john*))
	 * "john smith"	-> (&(|(givenName=*john*)(sn=*john*)(cn=*john*))(|(givenName=*smith*)(sn=*smith*)(cn=*smith*)))
	 *
	 * @param query space-delimited query string
	 * @return a set of matching users from OID
	 */
	@Override
	public List<SearchResult> search(String query, List<String> excludedUsernames)
	{
		AndFilter filter = Stream.of(query.split(" "))
				.map(token -> query().where("givenName").whitespaceWildcardsLike(token)
							.or("sn").whitespaceWildcardsLike(token)
							.or("cn").whitespaceWildcardsLike(token))
				.collect(AndFilter::new, (f, q) -> f.and(q.filter()), AndFilter::and);

		for (String excludedUsername: excludedUsernames.subList(0, min(50, excludedUsernames.size())))
		{
			filter = filter.and(query().where("cn").not().is(excludedUsername).filter());
		}

		filter = filter.and(query().where("objectclass").not().is("alias").filter());

		if (log.isDebugEnabled())
		{
			val filterString = filter.encode();
			log.debug("Searching OID: {}", filterString);
			log.debug("Filter length={}", filterString.length());
			log.debug("Excluded usernames: {}", excludedUsernames);
		}

		val results = stream(userRepository
				.findAll(query()
						.base(USER_BASE)
						.filter(filter))
				.spliterator(), false)
				.filter(u -> !excludedUsernames.contains(u.getUsername()))
				.map(u -> SearchResult.builder()
						.username(u.getUsername())
						.aliasUsername(userAliasRepository.findByAliasedUserDn(u.getDn().toString() + "," + oidBase)
								.map(OIDUserAlias::getUsername).orElse(u.getUsername()))
						.score(deriveScore(query, u))
						.build())
				.collect(toList());
		log.debug("Found {} OID results", results.size());
		return results;
	}

	private float deriveScore(String query, OIDUser u)
	{
		return (float) Stream.of(query.split(" "))
				.map(String::toLowerCase)
				.mapToDouble(token -> Stream.of(u.getUsername(), u.getForenames(), u.getSurname())
						.filter(str -> !StringUtils.isEmpty(str))
						.filter(str -> str.toLowerCase().contains(token))
						.mapToDouble(item -> (double) token.length() / item.length())
						.max().orElse(0.0))
				.sum();
	}

	@Override
	public List<SearchResult> search(String query)
	{
		return search(query, Collections.emptyList());
	}

	@Override
	public Optional<OIDUser> getBasicUser(String username)
	{
		log.debug("Get basic OID user: {}", username);
		val r = userRepository.findByUsername(username);
		log.debug("Got basic OID user: {}", username);
		return r;
	}

	@Override
	public Optional<OIDUser> getUser(String username)
	{
		log.debug("Get OID user: {}", username);
		val user = getBasicUser(username)
				.map(u -> u.toBuilder()
						.roles(roleService.getRolesByParent(username, OIDUser.class).collect(toList()))
						.aliasUsername(getAlias(u.getDn()).orElse(username))
						.build());
		log.debug("Got OID user: {}", username);
		return user;
	}

	public Optional<String> getUsernameByAlias(String aliasUsername)
	{
		log.debug("Get username from alias: {}", aliasUsername);
		val r = userAliasRepository.getByUsername(aliasUsername)
				.map(OIDUserAlias::getAliasedUserDn)
				.map(dn -> {
					try
					{
						val name =  new LdapName(dn);
						return (String) name.getRdn(name.size() - 1).getValue();
					}
					catch (InvalidNameException e)
					{
						log.error("Error parsing alias user dn", e);
						return null;
					}
				});
		log.debug("Got username from alias: {}", aliasUsername);
		return r;
	}

	@Override
	public Optional<String> getAlias(String username)
	{
		return getBasicUser(username)
				.flatMap(u -> getAlias(u.getDn()));
	}

	private Optional<String> getAlias(Name userDn)
	{
		log.debug("Get alias: {}", userDn.toString());
		val r = userAliasRepository
				.findByAliasedUserDn(userDn.toString() + "," + oidBase)
				.map(OIDUserAlias::getUsername);
		log.debug("Got alias: {}", userDn.toString());
		return r;
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
		log.debug("Saving user: {}", user.getUsername());
		userRepository.save(user);

		// User alias
		log.debug("Deleting alias record exists");
		userAliasRepository.findByAliasedUserDn(String.format("cn=%s,%s,%s", user.getUsername(), USER_BASE, oidBase))
				.ifPresent(userAliasRepository::delete);
		if (user.getAliasUsername() != null && !user.getAliasUsername().equals(user.getUsername()))
		{
			log.debug("Creating alias record: {}", user.getAliasUsername());
			userAliasRepository.save(OIDUserAlias.builder()
					.username(user.getAliasUsername())
					.password(user.getPassword())
					.aliasedUserDn(user.getDn().toString() + "," + oidBase)
					.surname(user.getSurname())
					.sector(user.getSector())
					.build());
		}

		// Preferences
		log.debug("Checking if user preferences exist");
		if (!preferencesRepository.findOne(query()
				.searchScope(SearchScope.ONELEVEL)
				.base(String.format("cn=%s,%s", user.getUsername(), USER_BASE))).isPresent())
		{
			log.debug("Creating user preferences");
			preferencesRepository.save(new OIDUserPreferences(user.getUsername()));
		}

		// Role associations
		log.debug("Deleting existing role associations");
		roleAssociationRepository.deleteAll(roleAssociationRepository.findAll(query()
				.searchScope(SearchScope.ONELEVEL)
				.base(String.format("cn=%s,%s", user.getUsername(), USER_BASE))
				.where("objectclass").is("alias")));
		log.debug("Saving new role associations");
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
