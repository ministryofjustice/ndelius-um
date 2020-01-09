package uk.co.bconline.ndelius.config.security.provider.token;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
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
import static uk.co.bconline.ndelius.util.EncryptionUtils.decrypt;

@Slf4j
public class PreAuthenticatedTokenGranter extends AbstractTokenGranter {

	private static final String GRANT_TYPE = "preauthenticated";
	private String deliusSecret;

	public PreAuthenticatedTokenGranter(
			AuthorizationServerTokenServices tokenServices,
			ClientDetailsService clientDetailsService,
			OAuth2RequestFactory requestFactory,
			String deliusSecret) {
		super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
		this.deliusSecret = deliusSecret;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest)
	{
		Map<String, String> params = tokenRequest.getRequestParameters();
		if (!"NDelius".equals(client.getClientId())) {
			val e = new UnapprovedClientAuthenticationException("Pre-authenticated flow is only supported for client_id=delius");
			log.debug(e.getMessage(), e);
			throw e;
		}

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

		Authentication user = new UsernamePasswordAuthenticationToken(username, null, null);
		return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), user);
	}
}