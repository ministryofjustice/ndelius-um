package uk.co.bconline.ndelius.service;

import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.repository.ad.ADUserRepository;

@Slf4j
public abstract class ADUserDetailsService implements UserDetailsService
{
	private static final String USER_BASE = ADUser.class.getAnnotation(Entry.class).base();

	public abstract ADUserRepository getRepository();

	@Override
	public UserDetails loadUserByUsername(String username)
	{
		return getUser(stripDomain(username))
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)));
	}

	private static String stripDomain(String username)
	{
		return username.replaceAll("^(.*)@.*$", "$1");
	}

	public Optional<ADUser> getUser(String username)
	{
		log.debug("Fetching AD user {}", username);
		return getRepository().findByUsername(username);
	}

	public List<SearchResult> search(String query, List<String> excludedUsernames)
	{
		AndFilter filter = Stream.of(query.split(" "))
				.map(token -> query().where("samAccountName").whitespaceWildcardsLike(token))
				.collect(AndFilter::new, (f, q) -> f.and(q.filter()), AndFilter::and);

		for (String excludedUsername: excludedUsernames.subList(0, min(50, excludedUsernames.size())))
		{
			filter = filter.and(query().where("samAccountName").not().is(excludedUsername).filter());
		}

		if (log.isDebugEnabled())
		{
			val filterString = filter.encode();
			log.debug("Searching AD: {}", filterString);
			log.debug("Filter length={}", filterString.length());
			log.debug("Excluded usernames: {}", excludedUsernames);
		}

		val results = stream(getRepository()
				.findAll(query()
						.base(USER_BASE)
						.filter(filter))
				.spliterator(), false)
				.filter(u -> !excludedUsernames.contains(u.getUsername()))
				.map(u -> SearchResult.builder()
						.username(u.getUsername())
						.score(deriveScore(query, u))
						.build())
				.collect(toList());
		log.debug("Found {} AD results", results.size());
		return results;
	}

	private float deriveScore(String query, ADUser u)
	{
		return (float) Stream.of(query.split(" "))
				.map(String::toLowerCase)
				.mapToDouble(token -> {
					if (!isEmpty(u.getUsername()) && u.getUsername().toLowerCase().contains(token)) {
						return (float) token.length() / u.getUsername().length();
					}
					return 0f;
				})
				.sum();
	}

	public void save(ADUser adUser)
	{
		if (adUser.getUserAccountControl() == null)
		{
			int NORMAL_ACCOUNT = 0x0200;
			int PASSWD_NOTREQD = 0x0020;
			int DONT_EXPIRE_PASSWORD = 0x10000;
			adUser = adUser.toBuilder()
					.userAccountControl(Integer.toString(NORMAL_ACCOUNT + PASSWD_NOTREQD + DONT_EXPIRE_PASSWORD))
					.build();
		}
		getRepository().save(adUser);
	}

}
