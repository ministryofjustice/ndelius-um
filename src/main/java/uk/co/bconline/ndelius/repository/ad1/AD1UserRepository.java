package uk.co.bconline.ndelius.repository.ad1;

import java.util.Optional;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ADUser;

public interface AD1UserRepository extends LdapRepository<ADUser>
{
	Optional<ADUser> findByUsername(String username);
}
