package uk.co.bconline.ndelius.config.data.embedded;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldif.LDIFReader;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;
import uk.co.bconline.ndelius.config.data.embedded.interceptor.AliasInterceptor;
import uk.co.bconline.ndelius.config.data.embedded.interceptor.MemberOfInterceptor;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The built in unboundid embedded LDAP does not support alias de-referencing or dynamic group membership. This class is
 * a customized version of EmbeddedLdapAutoConfiguration to add custom request interceptors, which will perform alias
 * de-referencing and group membership lookups in the embedded LDAP.
 *
 * Note: This overrides the InMemoryDirectoryServer bean, which requires `spring.main.allow-bean-definition-overriding`
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ LdapProperties.class, EmbeddedLdapProperties.class })
@AutoConfigureBefore(LdapAutoConfiguration.class)
@ConditionalOnClass(InMemoryDirectoryServer.class)
@Conditional(EmbeddedLdapServer.EmbeddedLdapCondition.class)
public class EmbeddedLdapServer {

	private static final String PROPERTY_SOURCE_NAME = "ldap.ports";

	private final EmbeddedLdapProperties embeddedProperties;
	private final AliasInterceptor aliasInterceptor;
	private final MemberOfInterceptor memberOfInterceptor;

	private InMemoryDirectoryServer server;

	public EmbeddedLdapServer(
			EmbeddedLdapProperties embeddedProperties,
			AliasInterceptor aliasInterceptor,
			MemberOfInterceptor memberOfInterceptor) {
		this.embeddedProperties = embeddedProperties;
		this.aliasInterceptor = aliasInterceptor;
		this.memberOfInterceptor = memberOfInterceptor;
	}

	@Bean
	public InMemoryDirectoryServer directoryServer(ApplicationContext applicationContext) throws LDAPException {
		String[] baseDn = StringUtils.toStringArray(this.embeddedProperties.getBaseDn());
		InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(baseDn);
		if (hasCredentials(this.embeddedProperties.getCredential())) {
			config.addAdditionalBindCredentials(this.embeddedProperties.getCredential().getUsername(),
					this.embeddedProperties.getCredential().getPassword());
		}
		setSchema(config);
		InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig("LDAP",
				this.embeddedProperties.getPort());
		config.setListenerConfigs(listenerConfig);

		config.addInMemoryOperationInterceptor(aliasInterceptor);
		config.addInMemoryOperationInterceptor(memberOfInterceptor);
		this.server = new InMemoryDirectoryServer(config);
		aliasInterceptor.setServer(this.server);
		memberOfInterceptor.setServer(this.server);

		importLdif(applicationContext);
		this.server.startListening();
		setPortProperty(applicationContext, this.server.getListenPort());
		return this.server;
	}

	private void setSchema(InMemoryDirectoryServerConfig config) {
		if (!this.embeddedProperties.getValidation().isEnabled()) {
			config.setSchema(null);
			return;
		}
		Resource schema = this.embeddedProperties.getValidation().getSchema();
		if (schema != null) {
			setSchema(config, schema);
		}
	}

	private void setSchema(InMemoryDirectoryServerConfig config, Resource resource) {
		try {
			Schema defaultSchema = Schema.getDefaultStandardSchema();
			Schema schema = Schema.getSchema(resource.getInputStream());
			config.setSchema(Schema.mergeSchemas(defaultSchema, schema));
		}
		catch (Exception ex) {
			throw new IllegalStateException("Unable to load schema " + resource.getDescription(), ex);
		}
	}

	private boolean hasCredentials(EmbeddedLdapProperties.Credential credential) {
		return StringUtils.hasText(credential.getUsername()) && StringUtils.hasText(credential.getPassword());
	}

	private void importLdif(ApplicationContext applicationContext) throws LDAPException {
		String location = this.embeddedProperties.getLdif();
		if (StringUtils.hasText(location)) {
			try {
				Resource resource = applicationContext.getResource(location);
				if (resource.exists()) {
					try (InputStream inputStream = resource.getInputStream()) {
						this.server.importFromLDIF(true, new LDIFReader(inputStream));
					}
				}
			}
			catch (Exception ex) {
				throw new IllegalStateException("Unable to load LDIF " + location, ex);
			}
		}
	}

	private void setPortProperty(ApplicationContext context, int port) {
		if (context instanceof ConfigurableApplicationContext) {
			MutablePropertySources sources = ((ConfigurableApplicationContext) context).getEnvironment()
					.getPropertySources();
			getLdapPorts(sources).put("local.ldap.port", port);
		}
		if (context.getParent() != null) {
			setPortProperty(context.getParent(), port);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getLdapPorts(MutablePropertySources sources) {
		PropertySource<?> propertySource = sources.get(PROPERTY_SOURCE_NAME);
		if (propertySource == null) {
			propertySource = new MapPropertySource(PROPERTY_SOURCE_NAME, new HashMap<>());
			sources.addFirst(propertySource);
		}
		return (Map<String, Object>) propertySource.getSource();
	}

	@PreDestroy
	public void close() {
		if (this.server != null) {
			this.server.shutDown(true);
		}
	}

	/**
	 * {@link SpringBootCondition} to determine when to apply embedded LDAP
	 * auto-configuration.
	 */
	static class EmbeddedLdapCondition extends SpringBootCondition {

		private static final Bindable<List<String>> STRING_LIST = Bindable.listOf(String.class);

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			ConditionMessage.Builder message = ConditionMessage.forCondition("Embedded LDAP");
			Environment environment = context.getEnvironment();
			if (environment != null && !Binder.get(environment).bind("spring.ldap.embedded.base-dn", STRING_LIST)
					.orElseGet(Collections::emptyList).isEmpty()) {
				return ConditionOutcome.match(message.because("Found base-dn property"));
			}
			return ConditionOutcome.noMatch(message.because("No base-dn property found"));
		}

	}

}
