package uk.co.bconline.ndelius.model.ldap;

import java.io.Serializable;
import java.util.Collection;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
@Entry(objectClasses = "user")
public final class ADUser implements UserDetails, Serializable
{
	@Id
	@JsonIgnore
	private Name dn;

	@Attribute(name="samAccountName")
	private String username;

	@JsonIgnore
	@Attribute(name="password")
	private String password;

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return null;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired()
	{
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked()
	{
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled()
	{
		return true;
	}
}