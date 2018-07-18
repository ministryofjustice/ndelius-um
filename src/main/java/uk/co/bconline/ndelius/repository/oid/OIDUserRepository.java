package uk.co.bconline.ndelius.repository.oid;

import java.util.Optional;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.model.ldap.projections.OIDUserHomeArea;

public interface OIDUserRepository extends LdapRepository<OIDUser>
{
	Optional<OIDUser> findByUsername(String username);
	Optional<OIDUserHomeArea> getOIDUserHomeAreaByUsername(String username);
}
