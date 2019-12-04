package uk.co.bconline.ndelius.repository.ldap;

import org.springframework.data.ldap.repository.LdapRepository;
import uk.co.bconline.ndelius.model.entry.RoleAssociationEntry;

public interface RoleAssociationRepository extends LdapRepository<RoleAssociationEntry>
{
}
