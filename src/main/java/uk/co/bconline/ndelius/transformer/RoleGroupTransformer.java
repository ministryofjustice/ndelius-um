package uk.co.bconline.ndelius.transformer;

import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.RoleGroup;
import uk.co.bconline.ndelius.model.entry.RoleGroupEntry;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Component
public class RoleGroupTransformer
{
	public RoleGroup map(RoleGroupEntry group){
		return RoleGroup.builder()
				.name(group.getName())
				.roles(ofNullable(group.getRoles())
						.map(list -> list.stream()
							.map(t -> Role.builder()
									.name(t.getName())
									.description(t.getDescription()).build())
							.collect(toList()))
						.orElse(null))
				.build();
	}
}
