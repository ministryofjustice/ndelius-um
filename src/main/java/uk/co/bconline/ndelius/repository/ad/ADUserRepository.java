package uk.co.bconline.ndelius.repository.ad;

import java.util.Optional;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ldap.ADUser;

public interface ADUserRepository extends LdapRepository<ADUser>
{
	Optional<ADUser> findByUsername(String username);
}
