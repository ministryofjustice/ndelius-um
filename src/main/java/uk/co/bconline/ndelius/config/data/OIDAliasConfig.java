package uk.co.bconline.ndelius.config.data;

import java.util.Collections;

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
import org.springframework.ldap.core.support.LdapContextSource;

import uk.co.bconline.ndelius.repository.oidalias.OIDUserAliasRepository;

@Configuration
@EnableLdapRepositories(basePackageClasses = OIDUserAliasRepository.class, ldapTemplateRef = "oidAlias")
public class OIDAliasConfig extends LdapAutoConfiguration
{
	@Autowired
	public OIDAliasConfig(@Qualifier("oidProperties") LdapProperties properties, Environment environment)
	{
		super(properties, environment);
	}

	@Override
	@Bean("oidAliasContextSource")
	public ContextSource ldapContextSource()
	{
		LdapContextSource ctx = (LdapContextSource) super.ldapContextSource();
		ctx.setBaseEnvironmentProperties(Collections.singletonMap("java.naming.ldap.derefAliases", "never"));
		return ctx;
	}

	@Bean("oidAlias")
	public LdapTemplate ldapTemplate(@Qualifier("oidAliasContextSource") ContextSource contextSource)
	{
		return new LdapTemplate(contextSource);
	}
}
