package uk.co.bconline.ndelius.model.ldap;

import java.io.Serializable;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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