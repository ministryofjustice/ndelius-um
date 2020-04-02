package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.entry.ClientEntry;
import uk.co.bconline.ndelius.repository.ldap.ClientEntryRepository;
import uk.co.bconline.ndelius.service.UserRoleService;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MILLIS;

@Slf4j
@Service
public class ClientEntryServiceImpl implements ClientDetailsService
{
	private final ClientEntryRepository clientEntryRepository;
	private final UserRoleService userRoleService;

	@Autowired
	public ClientEntryServiceImpl(
			ClientEntryRepository clientEntryRepository,
			UserRoleService userRoleService)
	{
		this.clientEntryRepository = clientEntryRepository;
		this.userRoleService = userRoleService;
	}

	public Optional<ClientEntry> getBasicClient(String clientId)
	{
		val t = LocalDateTime.now();
		Optional<ClientEntry> client = clientEntryRepository.findByClientId(clientId);
		log.trace("--{}ms	LDAP lookup", MILLIS.between(t, LocalDateTime.now()));
		return client;
	}

	public Optional<ClientEntry> getClient(String clientId)
	{
		return getBasicClient(clientId)
				.map(u -> u.toBuilder()
						.roles(userRoleService.getUserRoles(clientId + ",cn=EISUsers"))
						.build());
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		return getClient(clientId)
				.orElseThrow(() -> new NoSuchClientException(String.format("Client '%s' not found", clientId)));
	}

}
