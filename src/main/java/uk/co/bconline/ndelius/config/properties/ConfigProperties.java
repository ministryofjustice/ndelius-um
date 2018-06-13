package uk.co.bconline.ndelius.config.properties;

import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Configuration
public class ConfigProperties
{
	@Bean("oidProperties")
	@ConfigurationProperties("oid")
	public LdapProperties oidProperties()
	{
		return new LdapProperties();
	}

	@Bean("ad1Properties")
	@ConfigurationProperties("ad.primary")
	public LdapProperties adTargetProperties()
	{
		return new LdapProperties();
	}

	@Bean("ad2Properties")
	@ConfigurationProperties("ad.secondary")
	public LdapProperties adSourceProperties()
	{
		return new LdapProperties();
	}

	@Primary
	@Bean("ldapProperties")
	public LdapProperties ldapProperties()
	{
		return new LdapProperties();
	}
}
