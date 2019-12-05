package uk.co.bconline.ndelius.model.entry;

import lombok.Getter;
import lombok.Setter;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

import javax.naming.Name;
import java.util.Set;

@Getter
@Entry(objectClasses = {"NDRoleGroup", "top"}, base = "cn=ndRoleGroups")
public final class RoleGroupEntry
{
    @Id
    private Name dn;

    @Attribute(name="cn")
    private String name;

    @Setter
    @Transient
    private Set<RoleEntry> roles;
}