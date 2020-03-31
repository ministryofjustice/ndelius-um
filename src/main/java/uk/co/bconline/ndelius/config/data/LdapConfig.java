package uk.co.bconline.ndelius.config.data;

import lombok.val;
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
	@Bean
	@Override
	public LdapContextSource ldapContextSource(LdapProperties properties, Environment environment)
	{
		val ctxSource = super.ldapContextSource(properties, environment);
		val pooled = Boolean.parseBoolean(properties.getBaseEnvironment().getOrDefault("com.sun.jndi.ldap.connect.pool", "false"));
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
