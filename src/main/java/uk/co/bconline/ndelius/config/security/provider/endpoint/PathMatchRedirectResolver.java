package uk.co.bconline.ndelius.config.security.provider.endpoint;

import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.stereotype.Component;

/**
 * PathMatchRedirectResolver extends the default {@link DefaultRedirectResolver} to allow the Host part of a redirect
 * uri to be null. This allows clients to register a redirect_uri by path only eg. '/path/to/service', to support
 * clients running on the same domain (for example using load balancer forwarding).
 */
@Component
public class PathMatchRedirectResolver extends DefaultRedirectResolver {
	protected boolean hostMatches(String registered, String requested) {
		if (registered != null) return super.hostMatches(registered, requested);
		return requested == null;
	}
}
