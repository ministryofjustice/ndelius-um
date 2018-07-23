package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.Transaction;
import uk.co.bconline.ndelius.model.ldap.OIDBusinessTransaction;
import uk.co.bconline.ndelius.repository.oid.OIDRoleRepository;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.transformer.UserTransformer;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;

@Service
public class RoleServiceImpl implements RoleService
{
	private final OIDRoleRepository oidRoleRepository;
	private final UserTransformer userTransformer;

	@Autowired
	public RoleServiceImpl(OIDRoleRepository oidRoleRepository, UserTransformer userTransformer)
	{
		this.oidRoleRepository = oidRoleRepository;
		this.userTransformer = userTransformer;
	}

	@Override
	public Iterable<Transaction> getRoles()
	{
		return stream(oidRoleRepository
				.findAll(query()
						.searchScope(ONELEVEL)
						.base(OIDBusinessTransaction.class.getAnnotation(Entry.class).base())
						.where("objectclass").like("*")).spliterator(), false)
				.map(userTransformer::map)
				.collect(toList());
	}

	@Override
	public List<OIDBusinessTransaction> getTransactionsByParent(String parent, Class<?> parentClass)
	{
		return stream(oidRoleRepository
				.findAll(query()
						.searchScope(ONELEVEL)
						.base(String.format("cn=%s,%s", parent, parentClass.getAnnotation(Entry.class).base()))
						.where("objectclass").like("NDRole*"))
				.spliterator(), false)
				.collect(toList());
	}
}
