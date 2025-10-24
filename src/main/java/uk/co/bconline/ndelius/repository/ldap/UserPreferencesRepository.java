package uk.co.bconline.ndelius.repository.ldap;

import org.springframework.data.ldap.repository.LdapRepository;
import uk.co.bconline.ndelius.model.entry.UserPreferencesEntry;

public interface UserPreferencesRepository extends LdapRepository<UserPreferencesEntry> {
}
