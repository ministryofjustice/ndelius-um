package uk.co.bconline.ndelius.service.impl;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Optionals;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.entry.GroupEntry;
import uk.co.bconline.ndelius.repository.ldap.GroupRepository;
import uk.co.bconline.ndelius.service.GroupService;

import javax.naming.Name;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;
import static uk.co.bconline.ndelius.util.LdapUtils.OBJECTCLASS;

@Service
public class GroupServiceImpl implements GroupService {

	@Value("${spring.ldap.base}")
	private String ldapBase;

	@Value("${delius.ldap.base.groups}")
	private String groupsBase;

	private final GroupRepository groupRepository;

	@Autowired
	public GroupServiceImpl(GroupRepository groupRepository) {
		this.groupRepository = groupRepository;
	}

	@Override
	public Set<GroupEntry> getGroups() {
		return stream(groupRepository.findAll().spliterator(), false)
				.collect(toSet());
	}

	@Override
	public Set<GroupEntry> getGroups(String type) {
		return stream(groupRepository.findAll(LdapQueryBuilder.query()
				.base(LdapNameBuilder.newInstance(groupsBase).add("ou", type).build())
				.where(OBJECTCLASS).is("groupOfNames")).spliterator(), false)
				.collect(toSet());
	}

	@Override
	public Optional<GroupEntry> getGroup(String name) {
		Name groupName = LdapNameBuilder.newInstance(groupsBase)
				.add("cn", name)
				.build();
		return groupRepository.findById(groupName);
	}

	@Override
	public Optional<GroupEntry> getGroup(String type, String name) {
		Name groupName = LdapNameBuilder.newInstance(groupsBase)
				.add("ou", type)
				.add("cn", name)
				.build();
		return groupRepository.findById(groupName);
	}

	@Override
	public Set<GroupEntry> getGroups(Collection<Name> groupNames) {
		val base = LdapUtils.newLdapName(ldapBase);
		return groupNames.stream()
				.map(name -> LdapUtils.removeFirst(name, base))
				.map(groupRepository::findById)
				.flatMap(Optionals::toStream)
				.collect(toSet());
	}
}
