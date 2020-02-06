package uk.co.bconline.ndelius.config.security.provider.token;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Map;

import static java.lang.Long.parseLong;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.stream.Collectors.toSet;
import static uk.co.bconline.ndelius.util.EncryptionUtils.decrypt;

@Slf4j
public class PreAuthenticatedTokenGranter extends AbstractTokenGranter {

	private static final String GRANT_TYPE = "preauthenticated";

	private final UserDetailsService userDetailsService;
	private final String deliusSecret;

	public PreAuthenticatedTokenGranter(
			AuthorizationServerTokenServices tokenServices,
			ClientDetailsService clientDetailsService,
			UserDetailsService userDetailsService,
			OAuth2RequestFactory requestFactory,
			String deliusSecret) {
		super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
		this.userDetailsService = userDetailsService;
		this.deliusSecret = deliusSecret;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest)
	{
		Map<String, String> params = tokenRequest.getRequestParameters();

		if (deliusSecret == null
				|| StringUtils.isEmpty(params.get("u"))
				|| StringUtils.isEmpty(params.get("t"))) {
			val e = new PreAuthenticatedCredentialsNotFoundException("Missing request parameters");
			log.debug(e.getMessage(), e);
			throw e;
		}

		val timestamp = decrypt(params.get("t"), deliusSecret);
		val username = decrypt(params.get("u"), deliusSecret);
		if (timestamp == null || username == null)
		{
			val e = new BadCredentialsException("Unable to decrypt request parameters");
			log.debug(e.getMessage(), e);
			throw e;
		}

		if (Instant.ofEpochMilli(parseLong(timestamp)).isBefore(now().minus(2, HOURS)))
		{
			val e = new NonceExpiredException("Timestamp expired");
			log.debug(String.format("%s - username=%s, timestamp=%s", e.getMessage(), username, timestamp), e);
			throw e;
		}

		val user = userDetailsService.loadUserByUsername(username);
		val authenticationToken = new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
		// We have to manually do the user scope filtering here, as the `checkUserScopes` flag would only work if we
		// overrode the TokenEndpointAuthenticationFilter::extractCredentials method to include a special case for the
		// preauthenticated grant (as well as the existing password grant)
		tokenRequest.setScope(user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(item -> tokenRequest.getScope().isEmpty() || tokenRequest.getScope().contains(item))
				.filter(client.getScope()::contains)
				.collect(toSet()));
		return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
	}
}