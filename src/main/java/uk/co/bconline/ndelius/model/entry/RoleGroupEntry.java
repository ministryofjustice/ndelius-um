package uk.co.bconline.ndelius.model.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

import javax.naming.Name;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entry(objectClasses = {"NDRoleGroup"}, base = "delius.ldap.base.role-groups")
public final class RoleGroupEntry
{
    @Id
    private Name dn;

    @Attribute(name="cn")
    private String name;

    @Transient
    private Set<RoleEntry> roles;
}