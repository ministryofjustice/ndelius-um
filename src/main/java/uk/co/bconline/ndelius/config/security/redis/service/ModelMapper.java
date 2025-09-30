package uk.co.bconline.ndelius.config.security.redis.service;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2DeviceCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2UserCode;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.util.StringUtils;
import uk.co.bconline.ndelius.config.security.redis.entity.OAuth2AuthorizationCodeGrantAuthorization;
import uk.co.bconline.ndelius.config.security.redis.entity.OAuth2AuthorizationGrantAuthorization;
import uk.co.bconline.ndelius.config.security.redis.entity.OAuth2ClientCredentialsGrantAuthorization;
import uk.co.bconline.ndelius.config.security.redis.entity.OAuth2DeviceCodeGrantAuthorization;
import uk.co.bconline.ndelius.config.security.redis.entity.OAuth2PreAuthenticatedGrantAuthorization;
import uk.co.bconline.ndelius.config.security.redis.entity.OAuth2TokenExchangeGrantAuthorization;
import uk.co.bconline.ndelius.config.security.redis.entity.OAuth2UserConsent;
import uk.co.bconline.ndelius.config.security.redis.entity.OidcAuthorizationCodeGrantAuthorization;
import uk.co.bconline.ndelius.config.security.token.PreAuthenticatedGrantAuthenticationToken;

import java.security.Principal;

final class ModelMapper {

    static OAuth2UserConsent convertOAuth2UserConsent(OAuth2AuthorizationConsent authorizationConsent) {
        String id = authorizationConsent.getRegisteredClientId()
            .concat("-")
            .concat(authorizationConsent.getPrincipalName());
        return new OAuth2UserConsent(id, authorizationConsent.getRegisteredClientId(),
            authorizationConsent.getPrincipalName(), authorizationConsent.getAuthorities());
    }

    static OAuth2AuthorizationGrantAuthorization convertOAuth2AuthorizationGrantAuthorization(
        OAuth2Authorization authorization) {

        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(authorization.getAuthorizationGrantType())) {
            OAuth2AuthorizationRequest authorizationRequest = authorization
                .getAttribute(OAuth2AuthorizationRequest.class.getName());
            return authorizationRequest.getScopes().contains(OidcScopes.OPENID)
                ? convertOidcAuthorizationCodeGrantAuthorization(authorization)
                : convertOAuth2AuthorizationCodeGrantAuthorization(authorization);
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(authorization.getAuthorizationGrantType())) {
            return convertOAuth2ClientCredentialsGrantAuthorization(authorization);
        } else if (AuthorizationGrantType.DEVICE_CODE.equals(authorization.getAuthorizationGrantType())) {
            return convertOAuth2DeviceCodeGrantAuthorization(authorization);
        } else if (AuthorizationGrantType.TOKEN_EXCHANGE.equals(authorization.getAuthorizationGrantType())) {
            return convertOAuth2TokenExchangeGrantAuthorization(authorization);
        } else if (PreAuthenticatedGrantAuthenticationToken.PREAUTHENTICATED.equals(authorization.getAuthorizationGrantType().getValue())) {
            return convertOAuth2PreAuthenticatedGrantAuthorization(authorization);
        }
        return null;
    }

    static OAuth2AuthorizationGrantAuthorization convertOAuth2PreAuthenticatedGrantAuthorization(OAuth2Authorization authorization) {
        OAuth2AuthorizationGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);
        OAuth2AuthorizationGrantAuthorization.RefreshToken refreshToken = extractRefreshToken(authorization);

        return new OAuth2PreAuthenticatedGrantAuthorization(authorization.getId(),
            authorization.getRegisteredClientId(), authorization.getPrincipalName(),
            authorization.getAuthorizedScopes(), accessToken, refreshToken,
            authorization.getAttribute(Principal.class.getName()));
    }

    static OidcAuthorizationCodeGrantAuthorization convertOidcAuthorizationCodeGrantAuthorization(
        OAuth2Authorization authorization) {
        OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode authorizationCode = extractAuthorizationCode(
            authorization);
        OAuth2AuthorizationGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);
        OAuth2AuthorizationGrantAuthorization.RefreshToken refreshToken = extractRefreshToken(authorization);
        OidcAuthorizationCodeGrantAuthorization.IdToken idToken = extractIdToken(authorization);

        return new OidcAuthorizationCodeGrantAuthorization(authorization.getId(), authorization.getRegisteredClientId(),
            authorization.getPrincipalName(), authorization.getAuthorizedScopes(), accessToken, refreshToken,
            authorization.getAttribute(Principal.class.getName()),
            authorization.getAttribute(OAuth2AuthorizationRequest.class.getName()), authorizationCode,
            authorization.getAttribute(OAuth2ParameterNames.STATE), idToken);
    }

    static OAuth2AuthorizationCodeGrantAuthorization convertOAuth2AuthorizationCodeGrantAuthorization(
        OAuth2Authorization authorization) {

        OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode authorizationCode = extractAuthorizationCode(
            authorization);
        OAuth2AuthorizationGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);
        OAuth2AuthorizationGrantAuthorization.RefreshToken refreshToken = extractRefreshToken(authorization);

        return new OAuth2AuthorizationCodeGrantAuthorization(authorization.getId(),
            authorization.getRegisteredClientId(), authorization.getPrincipalName(),
            authorization.getAuthorizedScopes(), accessToken, refreshToken,
            authorization.getAttribute(Principal.class.getName()),
            authorization.getAttribute(OAuth2AuthorizationRequest.class.getName()), authorizationCode,
            authorization.getAttribute(OAuth2ParameterNames.STATE));
    }

    static OAuth2ClientCredentialsGrantAuthorization convertOAuth2ClientCredentialsGrantAuthorization(
        OAuth2Authorization authorization) {

        OAuth2AuthorizationGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);

        return new OAuth2ClientCredentialsGrantAuthorization(authorization.getId(),
            authorization.getRegisteredClientId(), authorization.getPrincipalName(),
            authorization.getAuthorizedScopes(), accessToken);
    }

    static OAuth2DeviceCodeGrantAuthorization convertOAuth2DeviceCodeGrantAuthorization(
        OAuth2Authorization authorization) {

        OAuth2AuthorizationGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);
        OAuth2AuthorizationGrantAuthorization.RefreshToken refreshToken = extractRefreshToken(authorization);
        OAuth2DeviceCodeGrantAuthorization.DeviceCode deviceCode = extractDeviceCode(authorization);
        OAuth2DeviceCodeGrantAuthorization.UserCode userCode = extractUserCode(authorization);

        return new OAuth2DeviceCodeGrantAuthorization(authorization.getId(), authorization.getRegisteredClientId(),
            authorization.getPrincipalName(), authorization.getAuthorizedScopes(), accessToken, refreshToken,
            authorization.getAttribute(Principal.class.getName()), deviceCode, userCode,
            authorization.getAttribute(OAuth2ParameterNames.SCOPE),
            authorization.getAttribute(OAuth2ParameterNames.STATE));
    }

    static OAuth2TokenExchangeGrantAuthorization convertOAuth2TokenExchangeGrantAuthorization(
        OAuth2Authorization authorization) {

        OAuth2AuthorizationGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);

        return new OAuth2TokenExchangeGrantAuthorization(authorization.getId(), authorization.getRegisteredClientId(),
            authorization.getPrincipalName(), authorization.getAuthorizedScopes(), accessToken);
    }

    static OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode extractAuthorizationCode(
        OAuth2Authorization authorization) {
        OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode authorizationCode = null;
        if (authorization.getToken(OAuth2AuthorizationCode.class) != null) {
            OAuth2Authorization.Token<OAuth2AuthorizationCode> oauth2AuthorizationCode = authorization
                .getToken(OAuth2AuthorizationCode.class);
            authorizationCode = new OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode(
                oauth2AuthorizationCode.getToken().getTokenValue(),
                oauth2AuthorizationCode.getToken().getIssuedAt(), oauth2AuthorizationCode.getToken().getExpiresAt(),
                oauth2AuthorizationCode.isInvalidated());
        }
        return authorizationCode;
    }

    static OAuth2AuthorizationGrantAuthorization.AccessToken extractAccessToken(OAuth2Authorization authorization) {
        OAuth2AuthorizationGrantAuthorization.AccessToken accessToken = null;
        if (authorization.getAccessToken() != null) {
            OAuth2Authorization.Token<OAuth2AccessToken> oauth2AccessToken = authorization.getAccessToken();
            OAuth2TokenFormat tokenFormat = null;
            if (OAuth2TokenFormat.SELF_CONTAINED.getValue()
                .equals(oauth2AccessToken.getMetadata(OAuth2TokenFormat.class.getName()))) {
                tokenFormat = OAuth2TokenFormat.SELF_CONTAINED;
            } else if (OAuth2TokenFormat.REFERENCE.getValue()
                .equals(oauth2AccessToken.getMetadata(OAuth2TokenFormat.class.getName()))) {
                tokenFormat = OAuth2TokenFormat.REFERENCE;
            }
            accessToken = new OAuth2AuthorizationGrantAuthorization.AccessToken(
                oauth2AccessToken.getToken().getTokenValue(), oauth2AccessToken.getToken().getIssuedAt(),
                oauth2AccessToken.getToken().getExpiresAt(), oauth2AccessToken.isInvalidated(),
                oauth2AccessToken.getToken().getTokenType(), oauth2AccessToken.getToken().getScopes(), tokenFormat,
                new OAuth2AuthorizationGrantAuthorization.ClaimsHolder(oauth2AccessToken.getClaims()));
        }
        return accessToken;
    }

    static OAuth2AuthorizationGrantAuthorization.RefreshToken extractRefreshToken(OAuth2Authorization authorization) {
        OAuth2AuthorizationGrantAuthorization.RefreshToken refreshToken = null;
        if (authorization.getRefreshToken() != null) {
            OAuth2Authorization.Token<OAuth2RefreshToken> oauth2RefreshToken = authorization.getRefreshToken();
            refreshToken = new OAuth2AuthorizationGrantAuthorization.RefreshToken(
                oauth2RefreshToken.getToken().getTokenValue(), oauth2RefreshToken.getToken().getIssuedAt(),
                oauth2RefreshToken.getToken().getExpiresAt(), oauth2RefreshToken.isInvalidated());
        }
        return refreshToken;
    }

    static OidcAuthorizationCodeGrantAuthorization.IdToken extractIdToken(OAuth2Authorization authorization) {
        OidcAuthorizationCodeGrantAuthorization.IdToken idToken = null;
        if (authorization.getToken(OidcIdToken.class) != null) {
            OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
            idToken = new OidcAuthorizationCodeGrantAuthorization.IdToken(oidcIdToken.getToken().getTokenValue(),
                oidcIdToken.getToken().getIssuedAt(), oidcIdToken.getToken().getExpiresAt(),
                oidcIdToken.isInvalidated(),
                new OAuth2AuthorizationGrantAuthorization.ClaimsHolder(oidcIdToken.getClaims()));
        }
        return idToken;
    }

    static OAuth2DeviceCodeGrantAuthorization.DeviceCode extractDeviceCode(OAuth2Authorization authorization) {
        OAuth2DeviceCodeGrantAuthorization.DeviceCode deviceCode = null;
        if (authorization.getToken(OAuth2DeviceCode.class) != null) {
            OAuth2Authorization.Token<OAuth2DeviceCode> oauth2DeviceCode = authorization
                .getToken(OAuth2DeviceCode.class);
            deviceCode = new OAuth2DeviceCodeGrantAuthorization.DeviceCode(oauth2DeviceCode.getToken().getTokenValue(),
                oauth2DeviceCode.getToken().getIssuedAt(), oauth2DeviceCode.getToken().getExpiresAt(),
                oauth2DeviceCode.isInvalidated());
        }
        return deviceCode;
    }

    static OAuth2DeviceCodeGrantAuthorization.UserCode extractUserCode(OAuth2Authorization authorization) {
        OAuth2DeviceCodeGrantAuthorization.UserCode userCode = null;
        if (authorization.getToken(OAuth2UserCode.class) != null) {
            OAuth2Authorization.Token<OAuth2UserCode> oauth2UserCode = authorization.getToken(OAuth2UserCode.class);
            userCode = new OAuth2DeviceCodeGrantAuthorization.UserCode(oauth2UserCode.getToken().getTokenValue(),
                oauth2UserCode.getToken().getIssuedAt(), oauth2UserCode.getToken().getExpiresAt(),
                oauth2UserCode.isInvalidated());
        }
        return userCode;
    }

    static OAuth2AuthorizationConsent convertOAuth2AuthorizationConsent(OAuth2UserConsent userConsent) {
        return OAuth2AuthorizationConsent.withId(userConsent.getRegisteredClientId(), userConsent.getPrincipalName())
            .authorities((authorities) -> authorities.addAll(userConsent.getAuthorities()))
            .build();
    }

    static void mapOAuth2AuthorizationGrantAuthorization(
        OAuth2AuthorizationGrantAuthorization authorizationGrantAuthorization,
        OAuth2Authorization.Builder builder) {

        if (authorizationGrantAuthorization instanceof OidcAuthorizationCodeGrantAuthorization authorizationGrant) {
            mapOidcAuthorizationCodeGrantAuthorization(authorizationGrant, builder);
        } else if (authorizationGrantAuthorization instanceof OAuth2AuthorizationCodeGrantAuthorization authorizationGrant) {
            mapOAuth2AuthorizationCodeGrantAuthorization(authorizationGrant, builder);
        } else if (authorizationGrantAuthorization instanceof OAuth2ClientCredentialsGrantAuthorization authorizationGrant) {
            mapOAuth2ClientCredentialsGrantAuthorization(authorizationGrant, builder);
        } else if (authorizationGrantAuthorization instanceof OAuth2DeviceCodeGrantAuthorization authorizationGrant) {
            mapOAuth2DeviceCodeGrantAuthorization(authorizationGrant, builder);
        } else if (authorizationGrantAuthorization instanceof OAuth2TokenExchangeGrantAuthorization authorizationGrant) {
            mapOAuth2TokenExchangeGrantAuthorization(authorizationGrant, builder);
        } else if (authorizationGrantAuthorization instanceof OAuth2PreAuthenticatedGrantAuthorization authorizationGrant) {
            mapOAuth2PreAuthenticatedGrantAuthorization(authorizationGrant, builder);
        }
    }

    static void mapOAuth2PreAuthenticatedGrantAuthorization(
        OAuth2PreAuthenticatedGrantAuthorization preAuthenticatedGrantAuthorization,
        OAuth2Authorization.Builder builder) {
        builder.id(preAuthenticatedGrantAuthorization.getId())
            .principalName(preAuthenticatedGrantAuthorization.getPrincipalName())
            .authorizationGrantType(new AuthorizationGrantType(PreAuthenticatedGrantAuthenticationToken.PREAUTHENTICATED))
            .authorizedScopes(preAuthenticatedGrantAuthorization.getAuthorizedScopes())
            .attribute(Principal.class.getName(), preAuthenticatedGrantAuthorization.getPrincipal());

        mapAccessToken(preAuthenticatedGrantAuthorization.getAccessToken(), builder);
        mapRefreshToken(preAuthenticatedGrantAuthorization.getRefreshToken(), builder);
    }

    static void mapOidcAuthorizationCodeGrantAuthorization(
        OidcAuthorizationCodeGrantAuthorization authorizationCodeGrantAuthorization,
        OAuth2Authorization.Builder builder) {

        mapOAuth2AuthorizationCodeGrantAuthorization(authorizationCodeGrantAuthorization, builder);
        mapIdToken(authorizationCodeGrantAuthorization.getIdToken(), builder);
    }

    static void mapOAuth2AuthorizationCodeGrantAuthorization(
        OAuth2AuthorizationCodeGrantAuthorization authorizationCodeGrantAuthorization,
        OAuth2Authorization.Builder builder) {

        builder.id(authorizationCodeGrantAuthorization.getId())
            .principalName(authorizationCodeGrantAuthorization.getPrincipalName())
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizedScopes(authorizationCodeGrantAuthorization.getAuthorizedScopes())
            .attribute(Principal.class.getName(), authorizationCodeGrantAuthorization.getPrincipal())
            .attribute(OAuth2AuthorizationRequest.class.getName(),
                authorizationCodeGrantAuthorization.getAuthorizationRequest());
        if (StringUtils.hasText(authorizationCodeGrantAuthorization.getState())) {
            builder.attribute(OAuth2ParameterNames.STATE, authorizationCodeGrantAuthorization.getState());
        }

        mapAuthorizationCode(authorizationCodeGrantAuthorization.getAuthorizationCode(), builder);
        mapAccessToken(authorizationCodeGrantAuthorization.getAccessToken(), builder);
        mapRefreshToken(authorizationCodeGrantAuthorization.getRefreshToken(), builder);
    }

    static void mapOAuth2ClientCredentialsGrantAuthorization(
        OAuth2ClientCredentialsGrantAuthorization clientCredentialsGrantAuthorization,
        OAuth2Authorization.Builder builder) {

        builder.id(clientCredentialsGrantAuthorization.getId())
            .principalName(clientCredentialsGrantAuthorization.getPrincipalName())
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .authorizedScopes(clientCredentialsGrantAuthorization.getAuthorizedScopes());

        mapAccessToken(clientCredentialsGrantAuthorization.getAccessToken(), builder);
    }

    static void mapOAuth2DeviceCodeGrantAuthorization(OAuth2DeviceCodeGrantAuthorization deviceCodeGrantAuthorization,
                                                      OAuth2Authorization.Builder builder) {

        builder.id(deviceCodeGrantAuthorization.getId())
            .principalName(deviceCodeGrantAuthorization.getPrincipalName())
            .authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
            .authorizedScopes(deviceCodeGrantAuthorization.getAuthorizedScopes());
        if (deviceCodeGrantAuthorization.getPrincipal() != null) {
            builder.attribute(Principal.class.getName(), deviceCodeGrantAuthorization.getPrincipal());
        }
        if (deviceCodeGrantAuthorization.getRequestedScopes() != null) {
            builder.attribute(OAuth2ParameterNames.SCOPE, deviceCodeGrantAuthorization.getRequestedScopes());
        }
        if (StringUtils.hasText(deviceCodeGrantAuthorization.getDeviceState())) {
            builder.attribute(OAuth2ParameterNames.STATE, deviceCodeGrantAuthorization.getDeviceState());
        }

        mapAccessToken(deviceCodeGrantAuthorization.getAccessToken(), builder);
        mapRefreshToken(deviceCodeGrantAuthorization.getRefreshToken(), builder);
        mapDeviceCode(deviceCodeGrantAuthorization.getDeviceCode(), builder);
        mapUserCode(deviceCodeGrantAuthorization.getUserCode(), builder);
    }

    static void mapOAuth2TokenExchangeGrantAuthorization(
        OAuth2TokenExchangeGrantAuthorization tokenExchangeGrantAuthorization,
        OAuth2Authorization.Builder builder) {

        builder.id(tokenExchangeGrantAuthorization.getId())
            .principalName(tokenExchangeGrantAuthorization.getPrincipalName())
            .authorizationGrantType(AuthorizationGrantType.TOKEN_EXCHANGE)
            .authorizedScopes(tokenExchangeGrantAuthorization.getAuthorizedScopes());

        mapAccessToken(tokenExchangeGrantAuthorization.getAccessToken(), builder);
    }

    static void mapAuthorizationCode(OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode authorizationCode,
                                     OAuth2Authorization.Builder builder) {
        if (authorizationCode == null) {
            return;
        }
        OAuth2AuthorizationCode oauth2AuthorizationCode = new OAuth2AuthorizationCode(authorizationCode.getTokenValue(),
            authorizationCode.getIssuedAt(), authorizationCode.getExpiresAt());
        builder.token(oauth2AuthorizationCode, (metadata) -> metadata
            .put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, authorizationCode.isInvalidated()));
    }

    static void mapAccessToken(OAuth2AuthorizationGrantAuthorization.AccessToken accessToken,
                               OAuth2Authorization.Builder builder) {
        if (accessToken == null) {
            return;
        }
        OAuth2AccessToken oauth2AccessToken = new OAuth2AccessToken(accessToken.getTokenType(),
            accessToken.getTokenValue(), accessToken.getIssuedAt(), accessToken.getExpiresAt(),
            accessToken.getScopes());
        builder.token(oauth2AccessToken, (metadata) -> {
            metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, accessToken.isInvalidated());
            metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, accessToken.getClaims().claims());
            metadata.put(OAuth2TokenFormat.class.getName(), accessToken.getTokenFormat().getValue());
        });
    }

    static void mapRefreshToken(OAuth2AuthorizationGrantAuthorization.RefreshToken refreshToken,
                                OAuth2Authorization.Builder builder) {
        if (refreshToken == null) {
            return;
        }
        OAuth2RefreshToken oauth2RefreshToken = new OAuth2RefreshToken(refreshToken.getTokenValue(),
            refreshToken.getIssuedAt(), refreshToken.getExpiresAt());
        builder.token(oauth2RefreshToken, (metadata) -> metadata
            .put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, refreshToken.isInvalidated()));
    }

    static void mapIdToken(OidcAuthorizationCodeGrantAuthorization.IdToken idToken,
                           OAuth2Authorization.Builder builder) {
        if (idToken == null) {
            return;
        }
        OidcIdToken oidcIdToken = new OidcIdToken(idToken.getTokenValue(), idToken.getIssuedAt(),
            idToken.getExpiresAt(), idToken.getClaims().claims());
        builder.token(oidcIdToken, (metadata) -> {
            metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, idToken.isInvalidated());
            metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims().claims());
        });
    }

    static void mapDeviceCode(OAuth2DeviceCodeGrantAuthorization.DeviceCode deviceCode,
                              OAuth2Authorization.Builder builder) {
        if (deviceCode == null) {
            return;
        }
        OAuth2DeviceCode oauth2DeviceCode = new OAuth2DeviceCode(deviceCode.getTokenValue(), deviceCode.getIssuedAt(),
            deviceCode.getExpiresAt());
        builder.token(oauth2DeviceCode, (metadata) -> metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME,
            deviceCode.isInvalidated()));
    }

    static void mapUserCode(OAuth2DeviceCodeGrantAuthorization.UserCode userCode, OAuth2Authorization.Builder builder) {
        if (userCode == null) {
            return;
        }
        OAuth2UserCode oauth2UserCode = new OAuth2UserCode(userCode.getTokenValue(), userCode.getIssuedAt(),
            userCode.getExpiresAt());
        builder.token(oauth2UserCode, (metadata) -> metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME,
            userCode.isInvalidated()));
    }
}
