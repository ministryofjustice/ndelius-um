package uk.co.bconline.ndelius.config.data;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.odm.annotations.Entry;
import org.thymeleaf.util.StringUtils;
import uk.co.bconline.ndelius.model.entry.UserEntry;
import uk.co.bconline.ndelius.repository.ldap.UserEntryRepository;
import uk.co.bconline.ndelius.util.ReflectionUtils;

@Slf4j
@Configuration
@EnableLdapRepositories(basePackageClasses = UserEntryRepository.class)
public class LdapConfig extends LdapAutoConfiguration {
	private final Environment environment;

	@Autowired
	public LdapConfig(Environment environment) {
		this.environment = environment;
	}

	@Bean
	@Override
	public LdapContextSource ldapContextSource(LdapProperties properties, Environment environment) {
		val ctxSource = super.ldapContextSource(properties, environment);
		val pooled = Boolean.parseBoolean(properties.getBaseEnvironment().getOrDefault("com.sun.jndi.ldap.connect.pool", "false"));
		ctxSource.setPooled(pooled);
		return ctxSource;
	}

	@Bean
	@Override
	public LdapTemplate ldapTemplate(ContextSource contextSource) {
		return new LdapTemplate(contextSource);
	}

	/*
	 * Spring-LDAP doesn't currently support dynamic `@Entry.base` attributes. This EventListener is a workaround to
	 * allow us to set the attribute dynamically per-Entry via our environment properties.
	 *
	 * It works by using reflection to look up all our @Entry classes, and replaces the `base` value directly when the
	 * application context is started/refreshed.
	 *
	 * This is quite a hacky solution, and should be replaced as soon as better support is added in Spring.
	 *
	 * See https://github.com/spring-projects/spring-ldap/issues/444
	 */
	@EventListener(ContextRefreshedEvent.class)
	public void updateBases() throws ReflectiveOperationException {
		// Get all classes annotated with @Entry
		val entryClasses = ReflectionUtils.findAllAnnotatedClasses(UserEntry.class.getPackage().getName(), Entry.class);
		for (val entryClass: entryClasses) {
			val entryAnnotation = entryClass.getAnnotation(Entry.class);
			if (StringUtils.isEmpty(entryAnnotation.base())) continue;
			// Resolve the base attribute as an environment property
			val newBase = environment.getProperty(entryAnnotation.base(), entryAnnotation.base());
			// Replace the @Entry annotation with the updated base attribute
			val attributes = AnnotationUtils.getAnnotationAttributes(entryClass, entryAnnotation);
			attributes.put("base", newBase);
			val newAnnotation = AnnotationUtils.synthesizeAnnotation(attributes, Entry.class, entryClass);
			ReflectionUtils.replaceClassLevelAnnotation(entryClass, Entry.class, newAnnotation);
			log.info("Updated base for {} to '{}'", entryClass, entryClass.getAnnotation(Entry.class).base());
		}
	}
}
