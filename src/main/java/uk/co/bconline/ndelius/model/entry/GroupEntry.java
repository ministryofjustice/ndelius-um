package uk.co.bconline.ndelius.model.entry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entry(objectClasses = {"groupOfNames"}, base = "delius.ldap.base.groups")
public final class GroupEntry implements Serializable {
	@Id
	private Name dn;

	@Attribute(name = "cn")
	private String name;

	@Attribute(name = "description")
	private String description;

	@Attribute(name = "member")
	private List<Name> members;
}