package uk.co.bconline.ndelius.model.ldap;

import java.io.Serializable;
import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entry(objectClasses = "top", base = "cn=ndRoleCatalogue,cn=Users")
public final class OIDBusinessTransaction implements Serializable
{
	@Id
	private Name dn;

	@Attribute(name="cn")
	private String name;

	@Attribute(name="aliasedObjectName")
	private String aliasedObjectName;

	@Attribute(name="UIBusinessInteractionCollection")
	private List<String> roles;

	@Attribute(name="description")
	private String description;
}