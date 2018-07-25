package uk.co.bconline.ndelius.config.data.embedded;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldif.LDIFReader;

import uk.co.bconline.ndelius.config.data.embedded.interceptor.AliasDereferencingInterceptor;

/**
 * The built in unboundid embedded LDAP does not support alias dereferencing. This class is a customized version of
 * EmbeddedLdapAutoConfiguration to add a request interceptor, which will perform alias dereferencing in the embedded
 * LDAPs.
 */
public abstract class AliasDereferencingEmbeddedLdap
{
	private static final String PROPERTY_SOURCE_NAME = "ldap.ports";

	private final EmbeddedLdapProperties embeddedProperties;
	private final ConfigurableApplicationContext applicationContext;

	protected InMemoryDirectoryServer server;

	public AliasDereferencingEmbeddedLdap(
			EmbeddedLdapProperties embeddedProperties,
			ConfigurableApplicationContext applicationContext)
	{
		this.embeddedProperties = embeddedProperties;
		this.applicationContext = applicationContext;
	}

	public InMemoryDirectoryServer directoryServer() throws LDAPException, IOException
	{
		String[] baseDn = StringUtils.toStringArray(this.embeddedProperties.getBaseDn());
		InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(baseDn);
		if (hasCredentials(this.embeddedProperties.getCredential())) {
			config.addAdditionalBindCredentials(
					this.embeddedProperties.getCredential().getUsername(),
					this.embeddedProperties.getCredential().getPassword());
		}
		setSchema(config);
		InMemoryListenerConfig listenerConfig = InMemoryListenerConfig
				.createLDAPConfig("LDAP", this.embeddedProperties.getPort());
		config.setListenerConfigs(listenerConfig);
		AliasDereferencingInterceptor interceptor = new AliasDereferencingInterceptor();
		config.addInMemoryOperationInterceptor(interceptor);
		this.server = new InMemoryDirectoryServer(config);
		interceptor.setServer(this.server);
		importLdif();
		this.server.startListening();
		setPortProperty(this.applicationContext, this.server.getListenPort());
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
			throw new IllegalStateException(
					"Unable to load schema " + resource.getDescription(), ex);
		}
	}

	private boolean hasCredentials(EmbeddedLdapProperties.Credential credential) {
		return StringUtils.hasText(credential.getUsername())
				&& StringUtils.hasText(credential.getPassword());
	}

	private void importLdif() throws LDAPException, IOException
	{
		String location = this.embeddedProperties.getLdif();
		if (StringUtils.hasText(location)) {
			Resource resource = this.applicationContext.getResource(location);
			if (resource.exists()) {
				try (InputStream inputStream = resource.getInputStream()) {
					this.server.importFromLDIF(true, new LDIFReader(inputStream));
				}
			}
		}
	}

	private void setPortProperty(ApplicationContext context, int port) {
		if (context instanceof ConfigurableApplicationContext) {
			MutablePropertySources sources = ((ConfigurableApplicationContext) context)
					.getEnvironment().getPropertySources();
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
}
