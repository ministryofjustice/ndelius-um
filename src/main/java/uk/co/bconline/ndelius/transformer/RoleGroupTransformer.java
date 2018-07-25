package uk.co.bconline.ndelius.transformer;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import org.springframework.stereotype.Component;

import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.RoleGroup;
import uk.co.bconline.ndelius.model.ldap.OIDRoleGroup;

@Component
public class RoleGroupTransformer
{
	public RoleGroup map(OIDRoleGroup group){
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
