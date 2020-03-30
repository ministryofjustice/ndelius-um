package uk.co.bconline.ndelius.transformer;

import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.Group;
import uk.co.bconline.ndelius.model.entry.GroupEntry;

import javax.naming.Name;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
public class GroupTransformer
{
	public Group map(GroupEntry entry)
	{
		return Group.builder()
				.name(entry.getName())
				.type(entry.getType())
				.description(entry.getDescription())
				.build();
	}

	public Group mapWithMembers(GroupEntry entry)
	{
		return map(entry).toBuilder()
				.members(entry.getMembers().stream()
						.map(member -> LdapUtils.getStringValue(member, "cn"))
						.sorted()
						.collect(toList()))
				.build();
	}

	public List<Group> map(Collection<GroupEntry> entries)
	{
		return ofNullable(entries)
				.map(list -> list.stream()
						.map(this::map)
						.sorted(Comparator.comparing(Group::getName))
						.collect(toList()))
				.orElse(null);
	}

	public Set<Name> mapToNames(Collection<Group> groups, String base)
	{
		return ofNullable(groups)
				.map(list -> list.stream()
						.map(group -> (Name) LdapNameBuilder.newInstance(base)
								.add("ou", group.getType())
								.add("cn", group.getName())
								.build())
						.collect(toSet()))
				.orElse(null);
	}
}
