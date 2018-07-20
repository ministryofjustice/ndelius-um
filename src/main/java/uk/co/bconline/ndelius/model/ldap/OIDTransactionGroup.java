package uk.co.bconline.ndelius.model.ldap;

import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.io.Serializable;

@Data
@Entry(objectClasses = "top", base = "cn=ndRoleGroups,cn=Users")
public final class OIDTransactionGroup implements Serializable
{
    @Id
    private Name dn;

    @Attribute(name="cn")
    private String name;
}