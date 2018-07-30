package uk.co.bconline.ndelius.repository.oid;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ldap.OIDRoleAssociation;

public interface OIDRoleAssociationRepository extends LdapRepository<OIDRoleAssociation>
{
}
