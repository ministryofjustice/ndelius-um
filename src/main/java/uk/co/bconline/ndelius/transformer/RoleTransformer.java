package uk.co.bconline.ndelius.transformer;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.ldap.OIDRole;

@Component
public class RoleTransformer
{
	public Role map(OIDRole oidRole)
	{
		return Role.builder()
				.name(oidRole.getName())
				.description(oidRole.getDescription())
				.interactions(!oidRole.getInteractions().isEmpty()? new ArrayList<>(oidRole.getInteractions()): null)
				.build();
	}

	public OIDRole map(Role role)
	{
		return OIDRole.builder()
				.name(role.getName())
				.description(role.getDescription())
				.build();
	}
}
