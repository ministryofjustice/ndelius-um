package uk.co.bconline.ndelius.config.data;

import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ConfigProperties
{
	@Bean("oidProperties")
	@ConfigurationProperties("oid")
	public LdapProperties oidProperties()
	{
		return new LdapProperties();
	}

	@Primary
	@Bean("ldapProperties")
	public LdapProperties ldapProperties()
	{
		return new LdapProperties();
	}

	@Bean("embeddedOidProperties")
	@ConfigurationProperties("embedded.oid")
	public EmbeddedLdapProperties embeddedOidProperties()
	{
		return new EmbeddedLdapProperties();
	}
}
