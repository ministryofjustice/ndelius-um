package uk.co.bconline.ndelius.config.security.introspector;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.*;

/**
 * The UMT API resource server runs in the same process as the UMT authorization server.
 *
 * This allows the resource server to skip the extra HTTP request for token introspection and validate the token in-process.
 */
@Component
public class InMemoryOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
	private final OAuth2AuthorizationService authorizationService;

	public InMemoryOpaqueTokenIntrospector(OAuth2AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	@Override
	public OAuth2IntrospectionAuthenticatedPrincipal introspect(String token) {
		OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
		if (authorization == null) {
			throw new OAuth2IntrospectionException("Token not found");
		}

		OAuth2AccessToken source = authorization.getAccessToken().getToken();
		Instant exp = source.getExpiresAt();
		if (exp != null && exp.isBefore(Instant.now())) {
			throw new OAuth2IntrospectionException("Token expired");
		}

		Map<String, Object> attributes = new HashMap<>();
		attributes.put(ACTIVE, true);
		attributes.put(USERNAME, authorization.getPrincipalName());
		attributes.put(CLIENT_ID, authorization.getRegisteredClientId());
        attributes.put(SCOPE, StringUtils.collectionToDelimitedString(source.getScopes(), " "));
		attributes.put("grant_type", authorization.getAuthorizationGrantType());
		if (source.getExpiresAt() != null) attributes.put(EXP, source.getExpiresAt());
		if (source.getIssuedAt() != null) attributes.put(IAT, source.getIssuedAt());

		List<GrantedAuthority> authorities = source.getScopes().stream()
				.map(s -> new SimpleGrantedAuthority("SCOPE_" + s))
				.collect(Collectors.toList());

		return new OAuth2IntrospectionAuthenticatedPrincipal(attributes, authorities);
	}
}
