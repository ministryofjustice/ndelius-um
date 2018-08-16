package uk.co.bconline.ndelius.repository.oid;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ldap.OIDUserPreferences;

public interface OIDUserPreferencesRepository extends LdapRepository<OIDUserPreferences>
{
}
