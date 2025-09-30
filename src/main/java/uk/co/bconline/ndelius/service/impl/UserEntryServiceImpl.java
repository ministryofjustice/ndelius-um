package uk.co.bconline.ndelius.service.impl;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entry.GroupEntry;
import uk.co.bconline.ndelius.model.entry.UserEntry;
import uk.co.bconline.ndelius.model.entry.UserPreferencesEntry;
import uk.co.bconline.ndelius.model.entry.projections.UserHomeAreaProjection;
import uk.co.bconline.ndelius.model.notification.HmppsDomainEventType;
import uk.co.bconline.ndelius.repository.ldap.UserEntryRepository;
import uk.co.bconline.ndelius.repository.ldap.UserPreferencesRepository;
import uk.co.bconline.ndelius.service.DomainEventService;
import uk.co.bconline.ndelius.service.GroupService;
import uk.co.bconline.ndelius.service.UserEntryService;
import uk.co.bconline.ndelius.service.UserRoleService;
import uk.co.bconline.ndelius.transformer.SearchResultTransformer;
import uk.co.bconline.ndelius.util.SearchUtils;

import javax.naming.Name;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toConcurrentMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;
import static uk.co.bconline.ndelius.util.LdapUtils.OBJECTCLASS;
import static uk.co.bconline.ndelius.util.NameUtils.join;

@Slf4j
@Service
public class UserEntryServiceImpl implements UserEntryService, UserDetailsService {

    @Value("${spring.ldap.base}")
    private String ldapBase;

    @Value("${delius.ldap.base.users}")
    private String usersBase;

    @Value("${spring.ldap.useOracleAttributes:#{true}}")
    private boolean useOracleAttributes;

    private final UserEntryRepository userRepository;
    private final UserPreferencesRepository preferencesRepository;
    private final UserRoleService userRoleService;
    private final GroupService groupService;
    private final LdapTemplate ldapTemplate;
    private final LdapTemplate exportLdapTemplate;
    private final SearchResultTransformer searchResultTransformer;
    private final DomainEventService domainEventService;

    @Autowired
    public UserEntryServiceImpl(
        UserEntryRepository userRepository,
        UserPreferencesRepository preferencesRepository,
        UserRoleService userRoleService,
        GroupService groupService,
        LdapTemplate ldapTemplate,
        @Qualifier("exportLdapTemplate") LdapTemplate exportLdapTemplate,
        SearchResultTransformer searchResultTransformer,
        DomainEventService domainEventService
    ) {
        this.userRepository = userRepository;
        this.preferencesRepository = preferencesRepository;
        this.userRoleService = userRoleService;
        this.groupService = groupService;
        this.ldapTemplate = ldapTemplate;
        this.exportLdapTemplate = exportLdapTemplate;
        this.searchResultTransformer = searchResultTransformer;
        this.domainEventService = domainEventService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUser(username)
            .map(UserEntry::toUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)));
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Search for a list of users with a single text query.
     * <p>
     * The search query will be tokenized on space, then each token will be AND matched with wildcards.
     * eg.
     * "john"		-> (|(givenName=john*)(sn=john*)(cn=john*))
     * "john smith"	-> (&(|(givenName=john*)(sn=john*)(cn=john*))(|(givenName=smith*)(sn=smith*)(cn=smith*)))
     * <p>
     * The query will then be suffixed with a filter to ensure the userHomeArea attribute is contained within the
     * provided set of datasets
     * eg.
     * "john", {"N01", "N02"}	-> (&(|(givenName=john*)(sn=john*)(cn=john*))(|(userHomeArea=N01)(userHomeArea=N02))
     *
     * @param query    space-delimited query string
     * @param datasets a set of dataset codes to search within
     * @return a set of matching users from LDAP
     */
    @Override
    public List<SearchResult> search(String query, boolean includeInactiveUsers, Set<String> datasets) {
        // For a search with no dataset filtering, we don't need to search LDAP as all the users will be returned by the DB search
        // (We only ever need to search LDAP to find users that match on userHomeArea or email - as these attributes do not exist in the DB)
        if (datasets.isEmpty() && !includeInactiveUsers && !SearchUtils.isEmailSearch(query)) return emptyList();

        // Build up tokenized filter on givenName (forenames), sn (surname) and cn (username)
        AndFilter filter = Stream.of(query.trim().split("\\s+"))
            .map(token -> query().where("givenName").like(token + '*')
                .or("sn").like(token + '*')
                .or("cn").like(token + '*')
                .or("mail").whitespaceWildcardsLike(token)
            )
            .collect(AndFilter::new, (f, q) -> f.and(q.filter()), AndFilter::and);

        // Add additional filter that userHomeArea must be contained in 'datasets'
        filter = filter.and(datasets.stream()
            .map(dataset -> query().where("userHomeArea").is(dataset))
            .collect(OrFilter::new, (f, q) -> f.or(q.filter()), OrFilter::or));

        if (log.isDebugEnabled()) {
            val filterString = filter.encode();
            log.debug("Searching LDAP: {}", filterString);
        }

        val t = now();
        val results = stream(userRepository
            .findAll(query()
                .searchScope(ONELEVEL)
                .base(usersBase)
                .filter(filter))
            .spliterator(), true)
            .map(u -> searchResultTransformer.map(u, deriveScore(query, u)))
            .collect(toList());

        log.debug("Found {} LDAP results in {}ms", results.size(), MILLIS.between(t, now()));
        return results;
    }

    @Override
    public Map<String, UserEntry> export() {
        return exportLdapTemplate.find(query()
                .base(usersBase)
                .searchScope(ONELEVEL)
                .where(OBJECTCLASS).isPresent(), UserEntry.class)
            .parallelStream()
            .collect(toConcurrentMap(UserEntry::getUsername, u -> u));
    }

    @Override
    public Optional<UserEntry> getBasicUser(String username) {
        val t = now();
        Optional<UserEntry> user = userRepository.findByUsername(username);
        if (useOracleAttributes) {
            user = user.map(u -> u.toBuilder()
                .startDate(u.getOracleStartDate())
                .endDate(u.getOracleEndDate())
                .oracleStartDate(null)
                .oracleEndDate(null).build());
        }
        log.trace("--{}ms	LDAP lookup", MILLIS.between(t, now()));
        return user;
    }

    @Override
    public Optional<UserEntry> getUser(String username) {
        return getBasicUser(username)
            .map(u -> u.toBuilder()
                .roles(userRoleService.getUserRoles(u.getUsername()))
                .groups(groupService.getGroups(u.getGroupNames()))
                .build());
    }

    @Override
    public String getUserHomeArea(String username) {
        return userRepository.getUserHomeAreaProjectionByUsername(username)
            .map(UserHomeAreaProjection::getHomeArea).orElse(null);
    }

    @Override
    public Set<GroupEntry> getUserGroups(String username) {
        return getBasicUser(username)
            .map(u -> groupService.getGroups(u.getGroupNames()))
            .orElse(emptySet());
    }

    private void updateUserGroups(String username, Set<Name> groups) {
        val userDn = LdapNameBuilder.newInstance(ldapBase).add(getDn(username)).build();
        val existingGroups = getBasicUser(username).map(UserEntry::getGroupNames).orElse(emptySet());
        val newGroups = ofNullable(groups).orElse(emptySet());
        val groupsToAdd = Sets.difference(newGroups, existingGroups);
        val groupsToRemove = Sets.difference(existingGroups, newGroups);
        // Note: We must use serial streams here, due to a bug in Spring LDAP meaning the commonPool loads the
        // incorrect DirContext class.
        // See https://github.com/spring-projects/spring-ldap/issues/501
        groupService.getGroups(groupsToAdd).stream()
            .peek(group -> group.getMembers().add(userDn))
            .forEach(groupService::save);
        groupService.getGroups(groupsToRemove).stream()
            .peek(group -> group.getMembers().remove(userDn))
            .forEach(groupService::save);
    }

    @Override
    public void save(UserEntry user) {
        // Save user
        val t = now();
        log.debug("Saving user: {}", user.getUsername());
        if (useOracleAttributes) {
            user = user.toBuilder()
                .oracleStartDate(user.getStartDate())
                .oracleEndDate(user.getEndDate())
                .startDate(null)
                .endDate(null).build();
        }
        userRepository.save(user);

        // Groups
        log.debug("Updating group memberships");
        updateUserGroups(user.getUsername(), user.getGroupNames());

        // Preferences
        log.debug("Checking if user preferences exist");
        if (preferencesRepository.findOne(query()
            .searchScope(ONELEVEL)
            .base(getDn(user.getUsername()))
            .where(OBJECTCLASS).isPresent()).isEmpty()) {
            log.debug("Creating user preferences");
            preferencesRepository.save(new UserPreferencesEntry(user.getUsername()));
        }

        // Role associations
        userRoleService.updateUserRoles(user.getUsername(), user.getRoles());

        log.debug("Finished saving user to LDAP in {}ms", MILLIS.between(t, now()));
    }

    @Override
    public void save(String existingUsername, UserEntry user) {
        // Keep hold of the new username, if it's different we'll rename it later
        val newUsername = user.getUsername();
        user.setUsername(existingUsername);

        // Save changes to the user
        save(user);

        // Rename user if required
        if (!existingUsername.equals(newUsername)) {
            val oldDn = user.getDn();
            val newDn = LdapNameBuilder.newInstance(getDn(newUsername)).build();
            log.debug("Renaming LDAP entry from {} to {}", oldDn, newDn);
            ldapTemplate.rename(oldDn, newDn);

            // Send Domain event
            val additionalInformation = Map.of(
                "fromUsername", existingUsername,
                "toUsername", newUsername
            );
            domainEventService.insertDomainEvent(HmppsDomainEventType.UMT_USERNAME_CHANGED, additionalInformation);
        }
    }

    private float deriveScore(String query, UserEntry u) {
        return (float) SearchUtils.streamTokens(query)
            .mapToDouble(token -> Stream.of(u.getUsername(), u.getForenames(), u.getSurname(), u.getEmail())
                .filter(StringUtils::hasLength)
                .filter(str -> str.toLowerCase().contains(token))
                .mapToDouble(item -> (double) token.length() / item.length())
                .max().orElse(0.0))
            .sum();
    }

    private String getDn(String username) {
        return join(",", "cn=" + username, usersBase);
    }
}
