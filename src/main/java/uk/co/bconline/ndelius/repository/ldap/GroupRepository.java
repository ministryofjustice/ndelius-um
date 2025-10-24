package uk.co.bconline.ndelius.repository.ldap;

import org.springframework.data.ldap.repository.LdapRepository;
import uk.co.bconline.ndelius.model.entry.GroupEntry;

public interface GroupRepository extends LdapRepository<GroupEntry> {
}
