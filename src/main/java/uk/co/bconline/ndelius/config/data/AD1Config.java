package uk.co.bconline.ndelius.config.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import uk.co.bconline.ndelius.repository.ad.ad1.AD1UserRepository;

@Configuration
@EnableLdapRepositories(basePackageClasses = AD1UserRepository.class, ldapTemplateRef = "ad1")
@ConditionalOnProperty("ad.primary.urls")
public class AD1Config extends LdapAutoConfiguration
{
	@Autowired
	public AD1Config(@Qualifier("ad1Properties") LdapProperties properties, Environment environment)
	{
		super(properties, environment);
	}

	@Override
	@Bean("ad1ContextSource")
	public LdapContextSource ldapContextSource()
	{
		return super.ldapContextSource();
	}

	@Bean("ad1")
	public LdapTemplate ldapTemplate(@Qualifier("ad1ContextSource") ContextSource contextSource)
	{
		return new LdapTemplate(contextSource);
	}
}
