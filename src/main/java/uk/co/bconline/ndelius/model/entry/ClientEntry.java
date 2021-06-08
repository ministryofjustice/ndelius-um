package uk.co.bconline.ndelius.model.entry;

import lombok.*;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import uk.co.bconline.ndelius.util.AuthUtils;

import javax.naming.Name;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "clientSecret")
@Entry(objectClasses = {"NDClient", "inetOrgPerson", "top"}, base = "delius.ldap.base.clients")
public final class ClientEntry implements ClientDetails {
	@Id
	private Name dn;

	@Setter
	@Attribute(name = "cn")
	private String clientId;

	@Attribute(name = "userPassword")
	private String clientSecret;

	@Attribute(name = "authorizedGrantType")
	private Set<String> authorizedGrantTypes;

	@Attribute(name = "resourceId")
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
		return AuthUtils.mapToScopes(roles)
				.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return AuthUtils.mapToAuthorities(roles)
				.collect(Collectors.<GrantedAuthority>toUnmodifiableSet());
	}

	@Override
	public boolean isAutoApprove(String scope) {
		return true;
	}
}