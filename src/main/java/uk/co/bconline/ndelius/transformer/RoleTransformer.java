package uk.co.bconline.ndelius.transformer;

import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.entry.RoleEntry;

import java.util.ArrayList;

@Component
public class RoleTransformer
{
	public Role map(RoleEntry roleEntry)
	{
		return Role.builder()
				.name(roleEntry.getName())
				.description(roleEntry.getDescription())
				.interactions(!roleEntry.getInteractions().isEmpty()? new ArrayList<>(roleEntry.getInteractions()): null)
				.build();
	}

	public RoleEntry map(Role role)
	{
		return RoleEntry.builder()
				.name(role.getName())
				.description(role.getDescription())
				.build();
	}
}
