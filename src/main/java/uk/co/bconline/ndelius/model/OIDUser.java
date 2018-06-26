package uk.co.bconline.ndelius.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
@Entry(objectClasses = "NDUser", base="cn=Users")
public final class OIDUser implements UserDetails, Serializable
{
	@Id
	@JsonIgnore
	private Name dn;

	@Attribute(name="cn")
	private String username;

	@Attribute(name="givenName")
	private String forenames;

	@Attribute(name="sn")
	private String surname;

	@JsonIgnore
	@Attribute(name="userPassword")
	private String password;

	@Transient
	private List<String> roles;

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