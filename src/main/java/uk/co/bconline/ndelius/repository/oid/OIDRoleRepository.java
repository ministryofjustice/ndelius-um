package uk.co.bconline.ndelius.repository.oid;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ldap.OIDRole;

public interface OIDRoleRepository extends LdapRepository<OIDRole>
{
}
