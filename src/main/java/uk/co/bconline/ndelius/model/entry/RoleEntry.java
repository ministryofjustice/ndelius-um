package uk.co.bconline.ndelius.model.entry;

import lombok.*;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"name", "interactions"})
@Entry(objectClasses = "NDRole", base = "delius.ldap.base.roles")
public final class RoleEntry implements Serializable
{
	@Id
	private Name dn;

	@Attribute(name="cn")
	private String name;

	@Attribute(name="description")
	private String description;

	@Attribute(name="sector")
	private String sector;

	@Attribute(name="adminlevel")
	private String adminLevel;

	@Attribute(name="level1")
	private boolean level1;

	@Attribute(name="level2")
	private boolean level2;

	@Attribute(name="level3")
	private boolean level3;

	@Attribute(name="UIBusinessInteractionCollection")
	private List<String> interactions;
}