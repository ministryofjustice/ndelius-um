package uk.co.bconline.ndelius.repository.oid;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.OIDBusinessTransaction;

public interface OIDRoleRepository extends LdapRepository<OIDBusinessTransaction>
{
}
