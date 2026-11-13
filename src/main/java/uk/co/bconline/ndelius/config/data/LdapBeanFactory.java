package uk.co.bconline.ndelius.config.data;

import java.util.Collections;
import java.util.Locale;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ldap.autoconfigure.LdapConnectionDetails;
import org.springframework.boot.ldap.autoconfigure.LdapProperties;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;

final class LdapBeanFactory {
    private LdapBeanFactory() {
    }

    static LdapContextSource contextSource(LdapConnectionDetails connectionDetails, LdapProperties properties,
                                           ObjectProvider<DirContextAuthenticationStrategy> authenticationStrategy,
                                           String username) {
        LdapContextSource source = new LdapContextSource();
        authenticationStrategy.ifUnique(source::setAuthenticationStrategy);
        if (username != null) source.setUserDn(username);
        if (connectionDetails.getPassword() != null) source.setPassword(connectionDetails.getPassword());
        if (connectionDetails.getBase() != null) source.setBase(connectionDetails.getBase());
        if (connectionDetails.getUrls() != null) source.setUrls(connectionDetails.getUrls());
        if (properties.getAnonymousReadOnly() != null) source.setAnonymousReadOnly(properties.getAnonymousReadOnly());
        if (properties.getReferral() != null) source.setReferral(properties.getReferral().name().toLowerCase(Locale.ROOT));
        source.setBaseEnvironmentProperties(Collections.unmodifiableMap(properties.getBaseEnvironment()));
        source.setPooled(Boolean.parseBoolean(properties.getBaseEnvironment().getOrDefault("com.sun.jndi.ldap.connect.pool", "false")));
        return source;
    }

    static LdapTemplate template(LdapProperties properties, ContextSource contextSource,
                                 ObjectDirectoryMapper objectDirectoryMapper) {
        LdapTemplate template = new LdapTemplate(contextSource);
        template.setObjectDirectoryMapper(objectDirectoryMapper);
        LdapProperties.Template settings = properties.getTemplate();
        template.setIgnorePartialResultException(settings.isIgnorePartialResultException());
        template.setIgnoreNameNotFoundException(settings.isIgnoreNameNotFoundException());
        template.setIgnoreSizeLimitExceededException(settings.isIgnoreSizeLimitExceededException());
        return template;
    }
}
