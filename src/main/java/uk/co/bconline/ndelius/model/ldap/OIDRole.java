package uk.co.bconline.ndelius.model.ldap;

import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entry(objectClasses = "top", base = "cn=ndRoleCatalogue,cn=Users")
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

	@Attribute(name="UIBusinessInteractionCollection")
	private List<String> interactions;
}