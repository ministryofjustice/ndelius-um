package uk.co.bconline.ndelius.service.impl;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uk.co.bconline.ndelius.model.OIDUser;
import uk.co.bconline.ndelius.repository.oid.OIDUserRepository;
import uk.co.bconline.ndelius.service.OIDUserService;

@Slf4j
@Service
public class OIDUserDetailsService implements OIDUserService, UserDetailsService
{
	private final OIDUserRepository repository;

	@Autowired
	public OIDUserDetailsService(OIDUserRepository repository)
	{
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		return repository
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

		return StreamSupport.stream(repository.findAll(query()
				.countLimit(pageSize * page)
				.filter(filter)).spliterator(), false)
				.skip(pageSize * (page-1))
				.collect(Collectors.toList());
	}
}
