package uk.co.bconline.ndelius.model.ldap;

import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Entry(objectClasses = "top", base = "cn=ndRoleGroups,cn=Users")
public final class OIDRoleGroup
{
    @Id
    private Name dn;

    @Attribute(name="cn")
    private String name;

    @Setter
    @Transient
    private List<OIDRole> roles;
}