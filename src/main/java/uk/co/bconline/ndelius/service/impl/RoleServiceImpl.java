package uk.co.bconline.ndelius.service.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.stereotype.Service;

import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.repository.oid.OIDRoleRepository;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.transformer.UserTransformer;

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
	public Iterable<Role> getRoles()
	{
		return stream(oidRoleRepository
				.findAll(query()
						.searchScope(ONELEVEL)
						.base(OIDRole.class.getAnnotation(Entry.class).base())
						.where("objectclass").like("*")).spliterator(), false)
				.map(userTransformer::map)
				.collect(toList());
	}

	@Override
	public List<OIDRole> getRolesByParent(String parent, Class<?> parentClass)
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
