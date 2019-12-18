package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.entry.RoleEntry;
import uk.co.bconline.ndelius.service.UserEntryService;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

@Slf4j
@Service
public class OAuthClientDetailsService implements ClientDetailsService
{
	private final UserEntryService userEntryService;

	@Autowired
	public OAuthClientDetailsService(
			UserEntryService userEntryService)
	{
		this.userEntryService = userEntryService;
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		return userEntryService
				.getUser(clientId)
				.map(user -> {
					val resourceIds = "oauth2-resource";
					val grantTypes = "client_credentials,refresh_token";
					val scopes = user.getRoles().parallelStream()
							.map(RoleEntry::getInteractions)
							.flatMap(List::stream)
							.filter(Objects::nonNull)
							.collect(joining(","));

					val base = new BaseClientDetails(clientId, resourceIds, scopes, grantTypes, scopes);
					base.setClientSecret(user.getPassword());
					base.setAutoApproveScopes(base.getScope());
					return base;
				}).orElseThrow(() -> new ClientRegistrationException(String.format("Client '%s' not found", clientId)));
	}
}
