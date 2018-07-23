package uk.co.bconline.ndelius.model.ldap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.*;

import javax.naming.Name;
import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entry(objectClasses = {"NDRoleAssociation", "alias", "top"}, base = "cn=Users")
public final class OIDBusinessTransactionAlias implements Serializable
{
	@Id
	private Name dn;

	@Attribute(name="cn")
	@DnAttribute(value="cn", index=2)
	private String name;

	@Transient
	@DnAttribute(value="cn", index=1)
	private String username;

	@Attribute(name="aliasedObjectName")
	private String aliasedObjectName;
}