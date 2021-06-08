package uk.co.bconline.ndelius.model.entry;

import lombok.*;
import org.springframework.ldap.odm.annotations.*;
import org.springframework.security.core.userdetails.UserDetails;
import uk.co.bconline.ndelius.model.auth.UserInteraction;
import uk.co.bconline.ndelius.model.entry.projections.UserHomeAreaProjection;
import uk.co.bconline.ndelius.util.AuthUtils;

import javax.naming.Name;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "password")
@Entry(objectClasses = {"NDUser", "inetOrgPerson", "top"}, base = "delius.ldap.base.users")
public final class UserEntry implements UserHomeAreaProjection, UserDetails {
	@Id
	private Name dn;

	@Setter
	@Attribute(name = "cn")
	@DnAttribute(value = "cn", index = 1)
	private String username;

	@Attribute(name = "uid")
	private String uid;

	@Attribute(name = "givenName")
	private String forenames;

	@Attribute(name = "sn")
	private String surname;

	@Attribute(name = "userHomeArea")
	private String homeArea;

	@Attribute(name = "userSector")
	private String sector;

	@Attribute(name = "userPassword")
	private String password;

	@Attribute(name = "mail")
	private String email;

	@Attribute
	private String telephoneNumber;

	// Oracle-specific start/end date (format=yyyyMMddHHmmss):
	@Attribute(name = "orclActiveStartDate")
	private String oracleStartDate;

	@Attribute(name = "orclActiveEndDate")
	private String oracleEndDate;

	// Non Oracle-specific start/end date (format=yyyyMMddHHmmss):
	@Attribute(name = "startDate")
	private String startDate;

	@Attribute(name = "endDate")
	private String endDate;

	@Attribute(name = "memberOf", readonly = true)
	private Set<Name> groupNames;

	@Transient
	private Set<GroupEntry> groups;

	@Transient
	private Set<RoleEntry> roles;

	@Override
	public Collection<UserInteraction> getAuthorities() {
		return AuthUtils.mapToAuthorities(roles)
				.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}