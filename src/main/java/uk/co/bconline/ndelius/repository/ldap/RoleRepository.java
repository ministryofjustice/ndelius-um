package uk.co.bconline.ndelius.repository.ldap;

import org.springframework.data.ldap.repository.LdapRepository;
import uk.co.bconline.ndelius.model.entry.RoleEntry;

import java.util.Optional;

public interface RoleRepository extends LdapRepository<RoleEntry> {
    Optional<RoleEntry> findByName(String name);
}
