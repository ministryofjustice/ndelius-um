package uk.co.bconline.ndelius.model.ldap;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entry(objectClasses = {"NDUser", "alias", "top"}, base="cn=Users")
@ToString(exclude = "password")
public final class OIDUserAlias
{
	@Id
	private Name dn;

	@Attribute(name="cn")
	@DnAttribute(value="cn", index=1)
	private String username;

	@Attribute(name="aliasedObjectName")
	private String aliasedUserDn;

	@Attribute(name="userSector")
	private String sector = "public";

	@Attribute(name="sn")
	private String surname = "alias";

	@Attribute(name="userPassword")
	private String password;
}