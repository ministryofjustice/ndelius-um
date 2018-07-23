package uk.co.bconline.ndelius.model.ldap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.io.Serializable;
import java.util.List;

@Getter
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