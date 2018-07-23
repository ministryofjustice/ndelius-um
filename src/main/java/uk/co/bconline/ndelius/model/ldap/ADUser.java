package uk.co.bconline.ndelius.model.ldap;

import java.io.Serializable;
import java.util.Collection;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "password")
@Entry(objectClasses = "person", base = "cn=Users")
public final class ADUser implements UserDetails, Serializable
{
	@Id
	private Name dn;

	@Attribute(name="samAccountName")
	@DnAttribute(value="cn", index=1)
	private String username;

	@Attribute(name="password")
	private String password;

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