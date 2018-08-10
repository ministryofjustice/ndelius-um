package uk.co.bconline.ndelius.service;

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
		return getRepository().findByUsername(username);
	}

	public List<SearchResult> search(String query, List<String> excludedUsernames)
	{
		AndFilter filter = Stream.of(query.split(" "))
				.map(token -> query().where("samAccountName").whitespaceWildcardsLike(token))
				.collect(AndFilter::new, (f, q) -> f.and(q.filter()), AndFilter::and);

		for (String excludedUsername: excludedUsernames)
		{
			filter = filter.and(query().where("samAccountName").not().is(excludedUsername).filter());
		}

		log.debug("Searching AD: {}", filter.encode());

		return stream(getRepository()
				.findAll(query()
						.base(USER_BASE)
						.filter(filter))
				.spliterator(), false)
				.map(u -> SearchResult.builder()
						.username(u.getUsername())
						.score(deriveScore(query, u))
						.build())
				.collect(toList());
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
		getRepository().save(adUser);
	}

}
