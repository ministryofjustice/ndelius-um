package uk.co.bconline.ndelius.config.security.provider;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import uk.co.bconline.ndelius.config.security.token.PreAuthenticatedGrantAuthenticationToken;

import java.security.Principal;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.stream.Collectors.toSet;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CLIENT_ID;
import static uk.co.bconline.ndelius.util.EncryptionUtils.decrypt;

@Slf4j
public class PreAuthenticatedGrantAuthenticationProvider implements AuthenticationProvider {

    private final RegisteredClientRepository registeredClientRepository;
    private final String deliusSecret;
    private final OAuth2TokenGenerator<?> tokenGenerator;
    private final OAuth2AuthorizationService authorizationService;
    private final UserDetailsService userDetailsService;

    public PreAuthenticatedGrantAuthenticationProvider(
        String deliusSecret,
        RegisteredClientRepository registeredClientRepository,
        OAuth2TokenGenerator<?> tokenGenerator,
        OAuth2AuthorizationService authorizationService,
        UserDetailsService userDetailsService
    ) {
        this.registeredClientRepository = registeredClientRepository;
        this.deliusSecret = deliusSecret;
        this.tokenGenerator = tokenGenerator;
        this.authorizationService = authorizationService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication request) throws AuthenticationException {
        PreAuthenticatedGrantAuthenticationToken authenticationRequest = (PreAuthenticatedGrantAuthenticationToken) request;

        // Ensure the client is authenticated
        val clientPrincipal = (Authentication) authenticationRequest.getPrincipal();
        if (!clientPrincipal.isAuthenticated()) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        // Ensure the client is configured to use this authorization grant type
        val clientId = clientPrincipal.getPrincipal().toString();
        val registeredClient = registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null || !registeredClient.getAuthorizationGrantTypes().contains(authenticationRequest.getGrantType())) {
            val e = new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT));
            log.debug(e.getMessage(), e);
            throw e;
        }

        // Validate the parameters
        val username = decrypt(authenticationRequest.getUsername(), deliusSecret);
        val timestamp = decrypt(authenticationRequest.getTimestamp(), deliusSecret);
        if (username == null || timestamp == null) {
            log.debug("Unable to decrypt request parameters");
            return null;
        }

        if (Instant.ofEpochMilli(Long.parseLong(timestamp)).isBefore(Instant.now().minus(2, HOURS))) {
            log.debug("Timestamp expired - username={}, timestamp={}", username, timestamp);
            return null;
        }

        // Grant the requested scopes that are available to both the client and the user. An empty scope request is treated as requesting all scopes.
        val userDetails = userDetailsService.loadUserByUsername(username);
        val scopes = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(item -> authenticationRequest.getScopes().isEmpty() || authenticationRequest.getScopes().contains(item))
            .filter(registeredClient.getScopes()::contains)
            .collect(toSet());

        // Build the authorization
        val userPrincipal = new UsernamePasswordAuthenticationToken(userDetails, null);
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
            .withRegisteredClient(registeredClient)
            .authorizedScopes(scopes)
            .attribute(CLIENT_ID, registeredClient.getClientId())
            .attribute(Principal.class.getName(), userPrincipal)
            .principalName(userDetails.getUsername())
            .authorizationGrantType(authenticationRequest.getGrantType());

        // Generate the access token and refresh token
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
            .registeredClient(registeredClient)
            .principal(userPrincipal)
            .authorizationServerContext(AuthorizationServerContextHolder.getContext())
            .authorizedScopes(scopes)
            .authorizationGrantType(authenticationRequest.getGrantType())
            .authorizationGrant(authenticationRequest);

        // Generate the access token
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build());
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                "The token generator failed to generate the access token.", null);
            throw new OAuth2AuthenticationException(error);
        }
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            generatedAccessToken.getTokenValue(),
            generatedAccessToken.getIssuedAt(),
            generatedAccessToken.getExpiresAt(),
            scopes
        );
        if (generatedAccessToken instanceof ClaimAccessor claimAccessor) {
            authorizationBuilder.token(accessToken, (metadata) -> {
                metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, claimAccessor.getClaims());
                metadata.put(OAuth2TokenFormat.class.getName(), OAuth2TokenFormat.REFERENCE.getValue());
            });
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        // ----- Refresh token -----
        OAuth2RefreshToken refreshToken = null;
        // Do not issue refresh token to public client
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build());
            if (generatedRefreshToken != null) {
                if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate a valid refresh token.", null);
                    throw new OAuth2AuthenticationException(error);
                }

                refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
                authorizationBuilder.refreshToken(refreshToken);
            }
        }

        // Save the authorization
        this.authorizationService.save(authorizationBuilder.build());

        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
