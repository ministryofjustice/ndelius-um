package uk.co.bconline.ndelius.config.security.redis.entity;

import java.util.Set;

public class OAuth2TokenExchangeGrantAuthorization extends OAuth2AuthorizationGrantAuthorization {

    public OAuth2TokenExchangeGrantAuthorization(String id, String registeredClientId, String principalName, Set<String> authorizedScopes,
                                                 AccessToken accessToken, Long ttl) {
        super(id, registeredClientId, principalName, authorizedScopes, accessToken, null, ttl);
    }
}
