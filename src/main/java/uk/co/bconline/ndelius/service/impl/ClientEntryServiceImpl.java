package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.*;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.entry.ClientEntry;
import uk.co.bconline.ndelius.repository.ldap.ClientEntryRepository;
import uk.co.bconline.ndelius.service.UserRoleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@Slf4j
@Service
public class ClientEntryServiceImpl implements ClientDetailsService, ClientRegistrationService
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

	@Override
	public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException
	{
		ClientEntry client = (ClientEntry) clientDetails;

		// Save user
		log.debug("Saving client: {}", client.getClientId());
		clientEntryRepository.save(client);

		// Role associations
		userRoleService.updateUserRoles(client.getClientId(), client.getRoles());
	}

	@Override
	public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
		if (!getBasicClient(clientDetails.getClientId()).isPresent()) {
			throw new NoSuchClientException(String.format("Client '%s' not found", clientDetails.getClientId()));
		}
		addClientDetails(clientDetails);
	}

	@Override
	public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
		getBasicClient(clientId)
				.map(c -> c.toBuilder().clientSecret(secret).build())
				.map(clientEntryRepository::save)
				.orElseThrow(() -> new NoSuchClientException(String.format("Client '%s' not found", clientId)));
	}

	@Override
	public void removeClientDetails(String clientId) throws NoSuchClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ClientDetails> listClientDetails() {
		return stream(clientEntryRepository.findAll().spliterator(), true)
				.map(c -> (ClientDetails) c)
				.collect(toList());
	}
}
