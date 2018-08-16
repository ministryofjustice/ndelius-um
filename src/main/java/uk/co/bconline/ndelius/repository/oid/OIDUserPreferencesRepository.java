package uk.co.bconline.ndelius.repository.oid;

import java.util.Optional;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ldap.OIDUserPreferences;

public interface OIDUserPreferencesRepository extends LdapRepository<OIDUserPreferences>
{
	Optional<OIDUserPreferences> findByUsername(String username);
}
