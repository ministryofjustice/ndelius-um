package uk.co.bconline.ndelius.model.entry;

import lombok.*;
import org.springframework.ldap.odm.annotations.*;
import org.springframework.security.core.userdetails.UserDetails;
import uk.co.bconline.ndelius.model.auth.UserInteraction;
import uk.co.bconline.ndelius.model.entry.projections.UserHomeAreaProjection;

import javax.naming.Name;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "password")
@Entry(objectClasses = {"NDUser", "inetOrgPerson", "top"})
public final class UserEntry implements UserHomeAreaProjection, UserDetails
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

	@Attribute(name="userPassword")
	private String password;

	@Attribute(name="mail")
	private String email;

	// Oracle-specific start/end date (format=yyyyMMddHHmmss):
	@Attribute(name="orclActiveStartDate")
	private String oracleStartDate;

	@Attribute(name="orclActiveEndDate")
	private String oracleEndDate;

	// Non Oracle-specific start/end date (format=yyyyMMddHHmmss):
	@Attribute(name="startDate")
	private String startDate;

	@Attribute(name="endDate")
	private String endDate;

	@Transient
	private Set<RoleEntry> roles;

	@Override
	public Collection<UserInteraction> getAuthorities()
	{
		return ofNullable(roles)
				.map(r -> r.parallelStream()
						.map(RoleEntry::getInteractions)
						.flatMap(List::stream)
						.map(UserInteraction::new)
						.collect(toSet()))
				.orElseGet(Collections::emptySet);
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