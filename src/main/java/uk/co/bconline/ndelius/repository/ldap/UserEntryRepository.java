package uk.co.bconline.ndelius.repository.ldap;

import org.springframework.data.ldap.repository.LdapRepository;
import uk.co.bconline.ndelius.model.entry.UserEntry;
import uk.co.bconline.ndelius.model.entry.projections.UserHomeAreaProjection;

import java.util.Optional;

public interface UserEntryRepository extends LdapRepository<UserEntry> {
    Optional<UserEntry> findByUsername(String username);

    Optional<UserHomeAreaProjection> getUserHomeAreaProjectionByUsername(String username);

    Optional<UserEntry> findByEmail(String email);
}
