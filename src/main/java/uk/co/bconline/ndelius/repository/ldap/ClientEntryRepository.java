package uk.co.bconline.ndelius.repository.ldap;

import org.springframework.data.ldap.repository.LdapRepository;
import uk.co.bconline.ndelius.model.entry.ClientEntry;

import java.util.Optional;

public interface ClientEntryRepository extends LdapRepository<ClientEntry> {
    Optional<ClientEntry> findByClientId(String clientId);
}
