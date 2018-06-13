package uk.co.bconline.ndelius.repository.ad2;

import java.util.Optional;

import org.springframework.data.ldap.repository.LdapRepository;

import uk.co.bconline.ndelius.model.ADUser;

public interface AD2UserRepository extends LdapRepository<ADUser>
{
	Optional<ADUser> findByUsername(String username);
}
