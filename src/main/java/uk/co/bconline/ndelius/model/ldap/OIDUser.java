package uk.co.bconline.ndelius.model.ldap;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.Set;

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
@Entry(objectClasses = {"NDUser", "person", "top"})
public final class OIDUser implements OIDUserHomeArea, UserDetails
{
	@Id
	private Name dn;

	@Setter
	@Attribute(name="cn")
	@DnAttribute(value="cn", index=0)
	private String username;

	@Attribute(name="uid")
	private String uid;

	@Attribute(name="givenName")
	private String forenames;

	@Attribute(name="sn")
	private String surname;

	@Attribute(name="userHomeArea")
	private String homeArea;

	@Attribute(name="userSector")
	private String sector;

	@Attribute(name="orclActiveEndDate") // format=yyyyMMddHHmmss
	private String endDate;

	@Attribute(name="userPassword")
	private String password;

	@Attribute(name="email")
	private String email;

	@Transient
	private String aliasUsername;

	@Transient
	private Set<OIDRole> roles;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return emptyList();
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