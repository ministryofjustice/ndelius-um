package uk.co.bconline.ndelius.model.entry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.*;

import javax.naming.Name;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entry(objectClasses = {"UserPreferences", "top"}, base = "delius.ldap.base.users")
public final class UserPreferencesEntry
{
	public UserPreferencesEntry(String username)
	{
		this.username = username;
	}

	@Id
	private Name dn;

	@Attribute(name="cn")
	@DnAttribute(value="cn", index=1)
	private String cn = "UserPreferences";

	@Transient
	@DnAttribute(value="cn", index=0)
	private String username;
}
