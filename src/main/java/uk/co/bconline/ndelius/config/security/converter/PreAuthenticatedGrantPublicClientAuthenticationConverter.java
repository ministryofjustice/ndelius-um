package uk.co.bconline.ndelius.config.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static uk.co.bconline.ndelius.config.security.token.PreAuthenticatedGrantAuthenticationToken.PREAUTHENTICATED;

@Slf4j
public class PreAuthenticatedGrantPublicClientAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!PREAUTHENTICATED.equals(grantType)) {
            return null;
        }

        Map<String, String[]> params = request.getParameterMap();

        // client_id (REQUIRED for public clients)
        val clientId = Stream.ofNullable(params.get(OAuth2ParameterNames.CLIENT_ID)).flatMap(Arrays::stream).findFirst().orElse(null);
        if (!StringUtils.hasText(clientId)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }

        Map<String, Object> additionalParameters = new HashMap<>();
        params.forEach((key, value) -> additionalParameters.put(key, (value.length == 1) ? value[0] : value));
        additionalParameters.remove(OAuth2ParameterNames.CLIENT_ID);

        return new OAuth2ClientAuthenticationToken(clientId, ClientAuthenticationMethod.NONE, null, additionalParameters);
    }
}
