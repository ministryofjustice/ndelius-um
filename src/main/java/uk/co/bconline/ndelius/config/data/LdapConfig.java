package uk.co.bconline.ndelius.config.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import uk.co.bconline.ndelius.repository.ldap.UserEntryRepository;

@Configuration
@EnableLdapRepositories(basePackageClasses = UserEntryRepository.class)
public class LdapConfig extends LdapAutoConfiguration
{
	private final Boolean pooled;

	@Autowired
	public LdapConfig(LdapProperties properties, Environment environment)
	{
		super(properties, environment);
		pooled = Boolean.parseBoolean(properties.getBaseEnvironment().getOrDefault("com.sun.jndi.ldap.connect.pool", "false"));
	}

	@Bean
	@Override
	public LdapContextSource ldapContextSource()
	{
		LdapContextSource ctxSource = super.ldapContextSource();
		ctxSource.setPooled(pooled);
		return ctxSource;
	}

	@Bean
	@Override
	public LdapTemplate ldapTemplate(ContextSource contextSource)
	{
		return new LdapTemplate(contextSource);
	}
}
