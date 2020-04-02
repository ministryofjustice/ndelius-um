package uk.co.bconline.ndelius.model.entry;

import lombok.*;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import uk.co.bconline.ndelius.model.auth.UserInteraction;

import javax.naming.Name;
import java.util.*;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "clientSecret")
@Entry(objectClasses = {"NDClient", "inetOrgPerson", "top"}, base = "delius.ldap.base.clients")
public final class ClientEntry implements ClientDetails
{
	@Id
	private Name dn;

	@Setter
	@Attribute(name="cn")
	private String clientId;

	@Attribute(name="userPassword")
	private String clientSecret;

	@Attribute(name="authorizedGrantType")
	private Set<String> authorizedGrantTypes;

	@Attribute(name="resourceId")
	private Set<String> resourceIds;

	@Attribute
	private Set<String> registeredRedirectUri;

	@Transient
	private Set<RoleEntry> roles;

	@Transient
	private Integer accessTokenValiditySeconds;

	@Transient
	private Integer refreshTokenValiditySeconds;

	@Transient
	private Map<String, Object> additionalInformation;

	@Override
	public boolean isSecretRequired() {
		return this.clientSecret != null;
	}

	@Override
	public boolean isScoped() {
		return this.roles != null && !this.roles.isEmpty();
	}

	@Override
	public Set<String> getScope() {
		return ofNullable(roles)
				.map(r -> r.parallelStream()
						.map(RoleEntry::getInteractions)
						.flatMap(List::stream)
						.collect(toSet()))
				.orElseGet(Collections::emptySet);
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities()
	{
		return ofNullable(roles)
				.map(r -> r.parallelStream()
						.map(RoleEntry::getInteractions)
						.flatMap(List::stream)
						.map(UserInteraction::new)
						.map(i -> (GrantedAuthority) i)
						.collect(toSet()))
				.orElseGet(Collections::emptySet);
	}

	@Override
	public boolean isAutoApprove(String scope) {
		return true;
	}
}