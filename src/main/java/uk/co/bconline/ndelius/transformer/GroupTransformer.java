package uk.co.bconline.ndelius.transformer;

import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.Group;
import uk.co.bconline.ndelius.model.entry.GroupEntry;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Component
public class GroupTransformer
{
	public Group map(GroupEntry entry)
	{
		return Group.builder()
				.name(entry.getName())
				.description(entry.getDescription())
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
}
