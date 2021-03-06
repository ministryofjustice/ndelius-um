package uk.co.bconline.ndelius.transformer;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.entry.RoleAssociationEntry;
import uk.co.bconline.ndelius.model.entry.RoleEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static uk.co.bconline.ndelius.util.NameUtils.join;

@Component
public class RoleTransformer {

	@Value("${spring.ldap.base}")
	private String ldapBase;

	@Value("${delius.ldap.base.roles}")
	private String rolesBase;

	public Role map(RoleEntry roleEntry) {
		return Role.builder()
				.name(roleEntry.getName())
				.description(roleEntry.getDescription())
				.interactions(!roleEntry.getInteractions().isEmpty() ? new ArrayList<>(roleEntry.getInteractions()) : null)
				.build();
	}

	public RoleEntry map(Role role) {
		return RoleEntry.builder()
				.name(role.getName())
				.description(role.getDescription())
				.build();
	}

	public List<Role> map(Collection<RoleEntry> roleEntries) {
		return ofNullable(roleEntries)
				.map(Collection::stream)
				.map(this::map)
				.orElse(null);
	}

	public List<Role> filterAndMap(Collection<RoleEntry> roleEntries, Collection<RoleEntry> filterRoles) {
		val filterRoleNames = filterRoles.stream().map(RoleEntry::getName).collect(toSet());
		return ofNullable(roleEntries)
				.map(Collection::stream)
				.map(stream -> stream.filter(role -> filterRoleNames.contains(role.getName())))
				.map(this::map)
				.orElse(null);
	}

	public List<Role> map(Stream<RoleEntry> roleEntries) {
		return ofNullable(roleEntries)
				.map(stream -> stream
						.map(this::map)
						.sorted(Comparator.comparing(Role::getName))
						.collect(toList()))
				.orElse(null);
	}

	public RoleAssociationEntry buildAssociation(String username, String roleName) {
		return RoleAssociationEntry.builder()
				.name(roleName)
				.username(username)
				.aliasedObjectName(join(",", "cn=" + roleName, rolesBase, ldapBase))
				.build();
	}
}
