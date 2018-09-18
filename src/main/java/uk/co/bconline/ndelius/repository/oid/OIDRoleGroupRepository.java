package uk.co.bconline.ndelius.repository.oid;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ldap.OIDRoleGroup;

public interface OIDRoleGroupRepository extends LdapRepository<OIDRoleGroup>
{
    Optional<OIDRoleGroup> findByName(String transactionGroupName);

    @Cacheable(value = "roleGroups", key = "'all'")
    Iterable<OIDRoleGroup> findAll();
}
