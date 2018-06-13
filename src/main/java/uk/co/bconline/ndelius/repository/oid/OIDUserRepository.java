package uk.co.bconline.ndelius.repository.oid;

import java.util.Optional;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.OIDUser;

public interface OIDUserRepository extends LdapRepository<OIDUser>
{
	Optional<OIDUser> findByUsername(String username);
}
