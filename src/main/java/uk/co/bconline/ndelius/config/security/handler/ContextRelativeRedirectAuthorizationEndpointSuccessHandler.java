package uk.co.bconline.ndelius.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * ContextRelativeRedirectAuthorizationEndpointSuccessHandler is a customized version of org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter::sendAuthorizationResponse
 * <p>
 * The only difference from the original is that it allows relative redirect URIs outside the `/umt/` context root.
 */
public class ContextRelativeRedirectAuthorizationEndpointSuccessHandler implements AuthenticationSuccessHandler {
    private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication = (OAuth2AuthorizationCodeRequestAuthenticationToken) authentication;
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .fromUriString(authorizationCodeRequestAuthentication.getRedirectUri())
            .queryParam(OAuth2ParameterNames.CODE,
                authorizationCodeRequestAuthentication.getAuthorizationCode().getTokenValue());
        if (StringUtils.hasText(authorizationCodeRequestAuthentication.getState())) {
            uriBuilder.queryParam(OAuth2ParameterNames.STATE,
                UriUtils.encode(authorizationCodeRequestAuthentication.getState(), StandardCharsets.UTF_8));
        }
        // build(true) -> Components are explicitly encoded
        String redirectUri = uriBuilder.build(true).toUriString();
        if (!UrlUtils.isAbsoluteUrl(redirectUri)) {
            // For relative paths, bypass the default redirect strategy and redirect to the path explicitly
            response.encodeRedirectURL(redirectUri);
            response.sendRedirect(redirectUri);
        } else {
            this.redirectStrategy.sendRedirect(request, response, redirectUri);
        }
    }
}
