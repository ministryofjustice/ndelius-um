package uk.co.bconline.ndelius.transformer;

import static java.util.stream.Collectors.toList;

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
				.interactions(oidRole.getName().startsWith("UMBT") || oidRole.getName().startsWith("UABT")?
						oidRole.getInteractions().stream().filter(i -> !i.equals("UMBI003")).collect(toList()): null)
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
