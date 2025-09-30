package uk.co.bconline.ndelius.config.security.introspector;

import lombok.val;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.*;

/**
 * The UMT API resource server runs in the same process as the UMT authorization server.
 * This allows the resource server to skip the extra HTTP request for token introspection and validate the token in-process.
 */
@Component
public class InProcessOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
	private final OAuth2AuthorizationService authorizationService;

    public InProcessOpaqueTokenIntrospector(OAuth2AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	@Override
	public OAuth2IntrospectionAuthenticatedPrincipal introspect(String token) {
        val authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
		if (authorization == null) {
            throw new BadOpaqueTokenException("Invalid access token");
		}

        val accessToken = authorization.getAccessToken();
		Map<String, Object> attributes = new HashMap<>();
        attributes.put(ACTIVE, accessToken.isActive());
		attributes.put(USERNAME, authorization.getPrincipalName());
		attributes.put(CLIENT_ID, authorization.getRegisteredClientId());
        attributes.put(SCOPE, StringUtils.collectionToDelimitedString(accessToken.getToken().getScopes(), " "));
		attributes.put("grant_type", authorization.getAuthorizationGrantType());
        if (accessToken.getToken().getExpiresAt() != null) attributes.put(EXP, accessToken.getToken().getExpiresAt());
        if (accessToken.getToken().getIssuedAt() != null) attributes.put(IAT, accessToken.getToken().getIssuedAt());

        List<GrantedAuthority> authorities = accessToken.getToken().getScopes().stream()
				.map(s -> new SimpleGrantedAuthority("SCOPE_" + s))
				.collect(Collectors.toList());

		return new OAuth2IntrospectionAuthenticatedPrincipal(attributes, authorities);
	}
}
