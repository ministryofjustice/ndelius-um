package uk.co.bconline.ndelius.repository.oid;

import org.springframework.data.ldap.repository.LdapRepository;
import uk.co.bconline.ndelius.model.ldap.OIDTransactionGroup;

import java.util.Optional;

public interface OIDRoleGroupRepository extends LdapRepository<OIDTransactionGroup>
{
    Optional<OIDTransactionGroup> findByName(String transactionGroupName);
}
