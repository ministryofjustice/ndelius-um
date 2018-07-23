package uk.co.bconline.ndelius.model.ldap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

import javax.naming.Name;
import java.io.Serializable;
import java.util.List;

@Getter
@Entry(objectClasses = "top", base = "cn=ndRoleGroups,cn=Users")
public final class OIDTransactionGroup implements Serializable
{
    @Id
    private Name dn;

    @Attribute(name="cn")
    private String name;

    @Setter
    @Transient
    private List<OIDBusinessTransaction> transactions;
}