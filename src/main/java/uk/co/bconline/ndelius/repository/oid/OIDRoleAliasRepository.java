package uk.co.bconline.ndelius.repository.oid;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ldap.OIDBusinessTransactionAlias;

public interface OIDRoleAliasRepository extends LdapRepository<OIDBusinessTransactionAlias>
{
}
