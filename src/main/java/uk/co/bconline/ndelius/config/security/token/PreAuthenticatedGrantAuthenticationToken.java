package uk.co.bconline.ndelius.config.security.token;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;
import java.util.Set;

@Getter
public class PreAuthenticatedGrantAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {
    public static final String PREAUTHENTICATED = "preauthenticated";
    public static final AuthorizationGrantType PREAUTHENTICATED_GRANT_TYPE = new AuthorizationGrantType(PREAUTHENTICATED);
    private final String username;
    private final String timestamp;
    private final Set<String> scopes;

    public PreAuthenticatedGrantAuthenticationToken(Authentication clientPrincipal, String username, String timestamp, Set<String> scopes) {
        super(PREAUTHENTICATED_GRANT_TYPE, clientPrincipal, Map.of(
            "username", username,
            "timestamp", timestamp,
            OAuth2ParameterNames.SCOPE, scopes
        ));
        this.username = username;
        this.timestamp = timestamp;
        this.scopes = scopes;
    }
}
