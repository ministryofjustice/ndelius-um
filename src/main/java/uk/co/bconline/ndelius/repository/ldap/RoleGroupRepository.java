package uk.co.bconline.ndelius.repository.ldap;

import org.springframework.data.ldap.repository.LdapRepository;
import uk.co.bconline.ndelius.model.entry.RoleGroupEntry;

import java.util.Optional;

public interface RoleGroupRepository extends LdapRepository<RoleGroupEntry>
{
    Optional<RoleGroupEntry> findByName(String transactionGroupName);
    Iterable<RoleGroupEntry> findAll();
}
