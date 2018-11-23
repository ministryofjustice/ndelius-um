package uk.co.bconline.ndelius.model.ldap;

import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import lombok.*;

@Getter
@Builder
@ToString(of = {"name", "dn"})
@NoArgsConstructor
@AllArgsConstructor
@Entry(objectClasses = "top", base = "cn=ndRoleCatalogue")
public final class OIDRole
{
	@Id
	private Name dn;

	@Attribute(name="cn")
	private String name;

	@Attribute(name="aliasedObjectName")
	private String aliasedObjectName;

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