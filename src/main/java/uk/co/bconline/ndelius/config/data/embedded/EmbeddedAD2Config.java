package uk.co.bconline.ndelius.config.data.embedded;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.ContextSource;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.sdk.LDAPException;

@Configuration
@EnableConfigurationProperties({ LdapProperties.class, EmbeddedLdapProperties.class })
@AutoConfigureBefore(LdapAutoConfiguration.class)
@ConditionalOnClass(InMemoryDirectoryServer.class)
@ConditionalOnProperty("embedded.ad.secondary.base-dn")
public class EmbeddedAD2Config extends AliasDereferencingEmbeddedLdap
{
	@Autowired
	public EmbeddedAD2Config(
			@Qualifier("embeddedAd2Properties") EmbeddedLdapProperties embeddedProperties,
			@Qualifier("ad2Properties") LdapProperties properties,
			ConfigurableApplicationContext applicationContext, Environment environment)
	{
		super(embeddedProperties, properties, applicationContext, environment);
	}

	@Bean
	@DependsOn("ad2DirectoryServer")
	@ConditionalOnMissingBean
	public ContextSource ldapContextSource() {
		return super.ldapContextSource();
	}

	@Bean(name = "ad2DirectoryServer")
	public InMemoryDirectoryServer ad1DirectoryServer() throws LDAPException
	{
		return super.directoryServer();
	}

	@PreDestroy
	public void close() {
		if (this.server != null) {
			this.server.shutDown(true);
		}
	}

}
