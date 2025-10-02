package uk.co.bconline.ndelius.model.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import uk.co.bconline.ndelius.util.AuthUtils;

import javax.naming.Name;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "clientSecret")
@Entry(objectClasses = {"NDClient", "inetOrgPerson", "top"}, base = "delius.ldap.base.clients")
public final class ClientEntry {
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

    public RegisteredClient toRegisteredClient() {
        return RegisteredClient
            .withId(clientId)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .clientAuthenticationMethods(methods -> {
                methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                if (authorizedGrantTypes != null && !authorizedGrantTypes.contains(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue())) {
                    methods.add(ClientAuthenticationMethod.NONE);
                }
            })
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
            .tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.REFERENCE).build())
            .scopes(scopes -> scopes.addAll(AuthUtils.mapToScopes(roles).collect(toSet())))
            .redirectUris(uris -> uris.addAll(registeredRedirectUri.stream()
                // Workaround for the new redirect strategy in spring authorization server. See org.springframework.security.web.DefaultRedirectStrategy
                .map(uri -> "/umt/".equals(uri) ? "/" : uri).collect(toSet())))
            .authorizationGrantTypes(types -> authorizedGrantTypes.stream().map(AuthorizationGrantType::new).forEach(types::add))
            .build();
    }
}
