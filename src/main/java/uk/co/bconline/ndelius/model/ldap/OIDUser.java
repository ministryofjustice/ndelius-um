package uk.co.bconline.ndelius.model.ldap;

import java.util.Collection;
import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.*;
import uk.co.bconline.ndelius.model.ldap.projections.OIDUserHomeArea;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "password")
@Entry(objectClasses = {"NDUser", "person", "top"}, base="cn=Users")
public final class OIDUser implements OIDUserHomeArea, UserDetails
{
	@Id
	private Name dn;

	@Setter
	@Attribute(name="cn")
	@DnAttribute(value="cn", index=1)
	private String username;

	@Attribute(name="givenName")
	private String forenames;

	@Attribute(name="sn")
	private String surname;

	@Attribute(name="userHomeArea")
	private String homeArea;

	@Attribute(name="userPassword")
	private String password;

	@Transient
	private String aliasUsername;

	@Transient
	private List<OIDRole> roles;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return null;
	}

	@Override
	public boolean isAccountNonExpired()
	{
		return true;
	}

	@Override
	public boolean isAccountNonLocked()
	{
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}
}