package uk.co.bconline.ndelius.security.filter;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.oauth2.common.util.OAuth2Utils.CLIENT_ID;
import static uk.co.bconline.ndelius.util.EncryptionUtils.decrypt;

@Slf4j
public class PreAuthenticatedRequestParameterFilter extends TokenEndpointAuthenticationFilter
{
	private static final AuthenticationManager NO_OP_AUTH_MANAGER = authentication -> {
		SecurityContextHolder.getContext().setAuthentication(authentication);
		authentication.setAuthenticated(true);
		return authentication;
	};

	private final OAuth2RequestFactory oAuth2RequestFactory;
	private final String secret;

	public PreAuthenticatedRequestParameterFilter(
			OAuth2RequestFactory oAuth2RequestFactory,
			String secret) {
		super(NO_OP_AUTH_MANAGER, oAuth2RequestFactory);
		this.oAuth2RequestFactory = oAuth2RequestFactory;
		this.secret = secret;
	}

	@Override
	protected Authentication extractCredentials(HttpServletRequest request)
	{
		if (secret == null
				|| StringUtils.isEmpty(request.getParameter("u"))
				|| StringUtils.isEmpty(request.getParameter("t"))) {
			log.debug("Parameters not provided, skipping pre-authentication filter");
			return super.extractCredentials(request);
		}

		val timestamp = decrypt(request.getParameter("t"), secret);
		val username = decrypt(request.getParameter("u"), secret);
		if (timestamp == null || username == null)
		{
			val e = new BadCredentialsException("Unable to decrypt request parameters");
			log.debug(e.getMessage(), e);
			throw e;
		}

		if (Instant.ofEpochMilli(Long.parseLong(timestamp))
				.isBefore(Instant.now().minus(2, ChronoUnit.HOURS)))
		{
			val e = new NonceExpiredException("Timestamp expired");
			log.debug(String.format("%s - username=%s, timestamp=%s", e.getMessage(), username, timestamp), e);
			throw e;
		}

		log.debug("Successfully verified pre-authentication parameters, authenticating the request using OAuth2");
		val authorizationParameters = getSingleValueMap(request);
		authorizationParameters.put(CLIENT_ID, username);
		val authorizationRequest = oAuth2RequestFactory.createAuthorizationRequest(authorizationParameters);
		authorizationRequest.setApproved(true);
		val oAuth2Request = oAuth2RequestFactory.createOAuth2Request(authorizationRequest);
		val authenticationToken = new UsernamePasswordAuthenticationToken(username, null, null);
		return new OAuth2Authentication(oAuth2Request, authenticationToken);
	}

	// Copied from TokenEndpointAuthenticationFilter.getSingleValueMap
	private Map<String, String> getSingleValueMap(HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		Map<String, String[]> parameters = request.getParameterMap();
		for (String key : parameters.keySet()) {
			String[] values = parameters.get(key);
			map.put(key, values != null && values.length > 0 ? values[0] : null);
		}
		return map;
	}
}
