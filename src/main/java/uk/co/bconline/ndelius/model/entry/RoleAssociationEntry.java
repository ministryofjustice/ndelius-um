package uk.co.bconline.ndelius.model.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

import javax.naming.Name;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entry(objectClasses = {"NDRoleAssociation", "alias"}, base = "delius.ldap.base.users")
public final class RoleAssociationEntry {
    @Id
    private Name dn;

    @Attribute
    private String cn;

    @Attribute(name = "cn")
    @DnAttribute(value = "cn", index = 1)
    private String name;

    @Transient
    @DnAttribute(value = "cn", index = 0)
    private String username;

    @Attribute(name = "aliasedObjectName")
    private String aliasedObjectName;
}
