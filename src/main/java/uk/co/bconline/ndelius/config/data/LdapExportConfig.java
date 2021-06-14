package uk.co.bconline.ndelius.config.data;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;

@Slf4j
@Configuration
public class LdapExportConfig extends LdapAutoConfiguration {
	@Value("${spring.ldap.export.username:${spring.ldap.username}}")
	private String exportUser;

	@Override
	@Bean("exportLdapContextSource")
	public LdapContextSource ldapContextSource(LdapProperties properties, Environment environment,
											   ObjectProvider<DirContextAuthenticationStrategy> dirContextAuthenticationStrategy) {
		properties.setUsername(exportUser);
		val ctxSource = super.ldapContextSource(properties, environment, dirContextAuthenticationStrategy);
		val pooled = Boolean.parseBoolean(properties.getBaseEnvironment().getOrDefault("com.sun.jndi.ldap.connect.pool", "false"));
		ctxSource.setPooled(pooled);
		return ctxSource;
	}

	@Override
	@Bean("exportLdapTemplate")
	public LdapTemplate ldapTemplate(LdapProperties properties, @Qualifier("exportLdapContextSource") ContextSource contextSource) {
		properties.setUsername(exportUser);
		return super.ldapTemplate(properties, contextSource);
	}
}
