package uk.co.bconline.ndelius.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.repository.ad.ADUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
public abstract class ADUserDetailsService implements UserDetailsService
{
	private static final String USER_BASE = ADUser.class.getAnnotation(Entry.class).base();
	private static final int NORMAL_ACCOUNT = 0x0200;
	private static final int PASSWD_NOTREQD = 0x0020;
	private static final int DONT_EXPIRE_PASSWORD = 0x10000;

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
		val t = LocalDateTime.now();
		val r = getRepository().findByUsername(username);
		log.trace("--{}ms	AD lookup", MILLIS.between(t, LocalDateTime.now()));
		return r;
	}

	public List<SearchResult> search(String query)
	{
		AndFilter filter = Stream.of(query.trim().split("\\s+"))
				.map(token -> query().where("givenName").like(token + '*')
						.or("sn").like(token + '*')
						.or("samAccountName").like(token + '*'))
				.collect(AndFilter::new, (f, q) -> f.and(q.filter()), AndFilter::and);

		if (log.isDebugEnabled())
		{
			val filterString = filter.encode();
			log.debug("Searching AD: {}", filterString);
		}

		val t = LocalDateTime.now();
		val results = stream(getRepository()
				.findAll(query()
						.base(USER_BASE)
						.filter(filter))
				.spliterator(), true)
				.map(u -> SearchResult.builder()
						.username(u.getUsername())
						.score(deriveScore(query, u))
						.build())
				.collect(toList());
		log.debug("Found {} AD results in {}ms", results.size(), MILLIS.between(t, LocalDateTime.now()));
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
			adUser = adUser.toBuilder()
					.userAccountControl(Integer.toString(NORMAL_ACCOUNT + PASSWD_NOTREQD + DONT_EXPIRE_PASSWORD))
					.build();
		}
		getRepository().save(adUser.toBuilder().cn(adUser.getUsername()).build());
	}

	public boolean usernameExists(String username)
	{
		return getRepository().findByUsername(username).isPresent();
	}
}
