package uk.co.bconline.ndelius.config.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;
import uk.co.bconline.ndelius.config.security.token.PreAuthenticatedGrantAuthenticationToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static uk.co.bconline.ndelius.config.security.token.PreAuthenticatedGrantAuthenticationToken.PREAUTHENTICATED;

@Slf4j
public class PreAuthenticatedGrantAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!PREAUTHENTICATED.equals(grantType)) {
            return null;
        }

        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

        Map<String, String[]> params = request.getParameterMap();
        val username = Stream.ofNullable(params.get("u")).flatMap(Arrays::stream).findFirst().orElse(null);
        val timestamp = Stream.ofNullable(params.get("t")).flatMap(Arrays::stream).findFirst().orElse(null);

        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(timestamp)) {
            val e = new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, "Missing request parameters 'u' or 't'", null));
            log.debug(e.getMessage(), e);
            throw e;
        }

        String scope = Stream.ofNullable(params.get(OAuth2ParameterNames.SCOPE)).flatMap(Arrays::stream).findFirst().orElse(null);
        if (StringUtils.hasText(scope) && params.get(OAuth2ParameterNames.SCOPE).length != 1) {
            val e = new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, "OAuth 2.0 Parameter: " + OAuth2ParameterNames.SCOPE, null));
            log.debug(e.getMessage(), e);
            throw e;
        }
        Set<String> scopes = StringUtils.hasText(scope) ? new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " "))) : Collections.emptySet();

        return new PreAuthenticatedGrantAuthenticationToken(clientPrincipal, username, timestamp, scopes);
    }
}
