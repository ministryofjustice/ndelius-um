package uk.co.bconline.ndelius.repository.oid;

import java.util.Optional;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ldap.OIDUserAlias;

public interface OIDUserAliasRepository extends LdapRepository<OIDUserAlias>
{
	Optional<OIDUserAlias> findByAliasedUserDn(String dn);
}
