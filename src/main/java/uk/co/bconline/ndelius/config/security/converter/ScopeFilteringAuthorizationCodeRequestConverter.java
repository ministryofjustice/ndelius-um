package uk.co.bconline.ndelius.config.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

/**
 * This class filters the scopes in the authorization_code request based on the user's authorities,
 * to ensure the access token does not grant a wider scope than the user is allowed access to.
 */
@Slf4j
public class ScopeFilteringAuthorizationCodeRequestConverter implements AuthenticationConverter {
    private final OAuth2AuthorizationCodeRequestAuthenticationConverter delegate = new OAuth2AuthorizationCodeRequestAuthenticationConverter();

    @Override
    public Authentication convert(HttpServletRequest request) {
        val token = (OAuth2AuthorizationCodeRequestAuthenticationToken) delegate.convert(request);
        if (token == null) return null;
        return new OAuth2AuthorizationCodeRequestAuthenticationToken(
            token.getAuthorizationUri(),
            token.getClientId(),
            (Authentication) token.getPrincipal(),
            token.getRedirectUri(),
            token.getState(),
            filteredScopes(token),
            token.getAdditionalParameters());
    }

    private Set<String> filteredScopes(OAuth2AuthorizationCodeRequestAuthenticationToken token) {
        if (token.getPrincipal() instanceof UsernamePasswordAuthenticationToken userPrincipal) {
            val userScopes = userPrincipal.getAuthorities().stream()
                .map(a -> a.getAuthority().replaceAll("^SCOPE_", ""))
                .collect(toUnmodifiableSet());
            return token.getScopes().stream().filter(userScopes::contains).collect(toUnmodifiableSet());
        }
        return token.getScopes();
    }
}
