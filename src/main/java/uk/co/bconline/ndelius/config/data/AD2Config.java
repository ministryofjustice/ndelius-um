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
import uk.co.bconline.ndelius.repository.ad.ad2.AD2UserRepository;

@Configuration
@EnableLdapRepositories(basePackageClasses = AD2UserRepository.class, ldapTemplateRef = "ad2")
@ConditionalOnProperty("ad.secondary.urls")
public class AD2Config extends LdapAutoConfiguration
{
	@Autowired
	public AD2Config(@Qualifier("ad2Properties") LdapProperties properties, Environment environment)
	{
		super(properties, environment);
	}

	@Override
	@Bean("ad2ContextSource")
	public LdapContextSource ldapContextSource()
	{
		return super.ldapContextSource();
	}

	@Bean("ad2")
	public LdapTemplate ldapTemplate(@Qualifier("ad2ContextSource") ContextSource contextSource)
	{
		return new LdapTemplate(contextSource);
	}
}
