package uk.co.bconline.ndelius.repository.oid;

import java.util.Optional;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ldap.OIDRole;

public interface OIDRoleRepository extends LdapRepository<OIDRole>
{
	Optional<OIDRole> findByName(String name);
}
