package uk.co.bconline.ndelius.config.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;

import uk.co.bconline.ndelius.repository.oid.OIDUserRepository;

@Configuration
@EnableLdapRepositories(basePackageClasses = OIDUserRepository.class, ldapTemplateRef = "oid")
public class OIDConfig extends LdapAutoConfiguration
{
	@Autowired
	public OIDConfig(@Qualifier("oidProperties") LdapProperties properties, Environment environment)
	{
		super(properties, environment);
	}

	@Override
	@Bean("oidContextSource")
	public ContextSource ldapContextSource()
	{
		return super.ldapContextSource();
	}

	@Bean("oid")
	public LdapTemplate ldapTemplate(@Qualifier("oidContextSource") ContextSource contextSource)
	{
		return new LdapTemplate(contextSource);
	}
}
