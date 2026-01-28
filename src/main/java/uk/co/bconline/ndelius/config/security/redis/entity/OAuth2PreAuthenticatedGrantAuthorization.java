package uk.co.bconline.ndelius.config.security.redis.entity;

import java.security.Principal;
import java.util.Set;

public class OAuth2PreAuthenticatedGrantAuthorization extends OAuth2AuthorizationGrantAuthorization {

    private final Principal principal;

    public OAuth2PreAuthenticatedGrantAuthorization(String id, String registeredClientId, String principalName, Set<String> authorizedScopes,
                                                    AccessToken accessToken, RefreshToken refreshToken, Principal principal, Long ttl) {
        super(id, registeredClientId, principalName, authorizedScopes, accessToken, refreshToken, ttl);
        this.principal = principal;
    }

    public Principal getPrincipal() {
        return this.principal;
    }
}
