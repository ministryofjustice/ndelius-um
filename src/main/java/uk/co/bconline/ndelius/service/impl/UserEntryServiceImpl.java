package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entry.UserEntry;
import uk.co.bconline.ndelius.model.entry.UserPreferencesEntry;
import uk.co.bconline.ndelius.model.entry.projections.UserHomeAreaProjection;
import uk.co.bconline.ndelius.repository.ldap.UserEntryRepository;
import uk.co.bconline.ndelius.repository.ldap.UserPreferencesRepository;
import uk.co.bconline.ndelius.service.UserEntryService;
import uk.co.bconline.ndelius.service.UserRoleService;
import uk.co.bconline.ndelius.transformer.SearchResultTransformer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;
import static uk.co.bconline.ndelius.util.AuthUtils.isNational;
import static uk.co.bconline.ndelius.util.LdapUtils.OBJECTCLASS;
import static uk.co.bconline.ndelius.util.NameUtils.join;

@Slf4j
@Service
public class UserEntryServiceImpl implements UserEntryService, UserDetailsService
{
	private static final String USER_BASE = UserEntry.class.getAnnotation(Entry.class).base();

	@Value("${spring.ldap.base}")
	private String ldapBase;

	@Value("${spring.ldap.useOracleAttributes:#{true}}")
	private boolean useOracleAttributes;

	private final UserEntryRepository userRepository;
	private final UserPreferencesRepository preferencesRepository;
	private final UserRoleService userRoleService;
	private final LdapTemplate ldapTemplate;
	private final SearchResultTransformer searchResultTransformer;

	@Autowired
	public UserEntryServiceImpl(
			UserEntryRepository userRepository,
			UserPreferencesRepository preferencesRepository,
			UserRoleService userRoleService,
			LdapTemplate ldapTemplate,
			SearchResultTransformer searchResultTransformer)
	{
		this.userRepository = userRepository;
		this.preferencesRepository = preferencesRepository;
		this.userRoleService = userRoleService;
		this.ldapTemplate = ldapTemplate;
		this.searchResultTransformer = searchResultTransformer;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
	{
		return getBasicUser(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)));
	}

	@Override
	public boolean usernameExists(String username)
	{
		return userRepository.findByUsername(username).isPresent();
	}

	/**
	 * Search for a list of users with a single text query.
	 *
	 * The search query will be tokenized on space, then each token will be AND matched with wildcards.
	 * eg.
	 * "john"		-> (|(givenName=john*)(sn=john*)(cn=john*))
	 * "john smith"	-> (&(|(givenName=john*)(sn=john*)(cn=john*))(|(givenName=smith*)(sn=smith*)(cn=smith*)))
	 *
	 * The query will then be suffixed with a filter to ensure the userHomeArea attribute is contained within the
	 * provided set of datasets
	 * eg.
	 * "john", {"N01", "N02"}	-> (&(|(givenName=john*)(sn=john*)(cn=john*))(|(userHomeArea=N01)(userHomeArea=N02))
	 *
	 * @param query space-delimited query string
	 * @param datasets a set of dataset codes to search within
	 * @return a set of matching users from LDAP
	 */
	@Override
	public List<SearchResult> search(String query, boolean includeInactiveUsers, Set<String> datasets)
	{
		// For a national search, we don't need to search LDAP as all the users will be returned by the DB search
		// (We only ever need to search LDAP to find users that match on userHomeArea - as that attribute doesn't exist in the DB)
		if (isNational() && !includeInactiveUsers) return emptyList();

		// Build up tokenized filter on givenName (forenames), sn (surname) and cn (username)
		AndFilter filter = Stream.of(query.trim().split("\\s+"))
				.map(token -> query().where("givenName").like(token + '*')
						.or("sn").like(token + '*')
						.or("cn").like(token + '*'))
				.collect(AndFilter::new, (f, q) -> f.and(q.filter()), AndFilter::and);

		// Add additional filter that userHomeArea must be contained in 'datasets'
		filter = filter.and(datasets.stream()
				.map(dataset -> query().where("userHomeArea").is(dataset))
				.collect(OrFilter::new, (f, q) -> f.or(q.filter()), OrFilter::or));

		if (log.isDebugEnabled())
		{
			val filterString = filter.encode();
			log.debug("Searching LDAP: {}", filterString);
		}

		val t = LocalDateTime.now();
		val results = stream(userRepository
				.findAll(query()
						.searchScope(ONELEVEL)
						.base(USER_BASE)
						.filter(filter))
				.spliterator(), true)
				.map(u -> searchResultTransformer.map(u, deriveScore(query, u)))
				.collect(toList());
		log.debug("Found {} LDAP results in {}ms", results.size(), MILLIS.between(t, LocalDateTime.now()));
		return results;
	}

	@Override
	public Optional<UserEntry> getBasicUser(String username)
	{
		val t = LocalDateTime.now();
		Optional<UserEntry> user = userRepository.findByUsername(username);
		if (useOracleAttributes) {
			user = user.map(u -> u.toBuilder()
					.startDate(u.getOracleStartDate())
					.endDate(u.getOracleEndDate())
					.oracleStartDate(null)
					.oracleEndDate(null).build());
		}
		log.trace("--{}ms	LDAP lookup", MILLIS.between(t, LocalDateTime.now()));
		return user;
	}

	@Override
	public Optional<UserEntry> getUser(String username)
	{
		return getBasicUser(username)
				.map(u -> u.toBuilder()
						.roles(userRoleService.getUserRoles(username))
						.build());
	}

	@Override
	public String getUserHomeArea(String username)
	{
		return userRepository.getUserHomeAreaProjectionByUsername(username).map(UserHomeAreaProjection::getHomeArea).orElse(null);
	}

	@Override
	public void save(UserEntry user)
	{
		// Save user
		log.debug("Saving user: {}", user.getUsername());
		if (useOracleAttributes) {
			user = user.toBuilder()
					.oracleStartDate(user.getStartDate())
					.oracleEndDate(user.getEndDate())
					.startDate(null)
					.endDate(null).build();
		}
		userRepository.save(user);

		// Preferences
		log.debug("Checking if user preferences exist");
		if (!preferencesRepository.findOne(query()
				.searchScope(ONELEVEL)
				.base(getDn(user.getUsername()))
				.where(OBJECTCLASS).isPresent()).isPresent())
		{
			log.debug("Creating user preferences");
			preferencesRepository.save(new UserPreferencesEntry(user.getUsername()));
		}

		// Role associations
		userRoleService.updateUserRoles(user.getUsername(), user.getRoles());

	}

	@Override
	public void save(String existingUsername, UserEntry user)
	{
		// Keep hold of the new username, if it's different we'll rename it later
		val newUsername = user.getUsername();
		user.setUsername(existingUsername);

		// Save changes to the user
		save(user);

		// Rename user if required
		if (!existingUsername.equals(newUsername))
		{
			val oldDn = user.getDn();
			val newDn = LdapNameBuilder.newInstance(getDn(newUsername)).build();
			log.debug("Renaming LDAP entry from {} to {}", oldDn, newDn);
			ldapTemplate.rename(oldDn, newDn);
		}
	}

	private float deriveScore(String query, UserEntry u)
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

	private String getDn(String username)
	{
		return join(",", "cn=" + username, USER_BASE);
	}
}
