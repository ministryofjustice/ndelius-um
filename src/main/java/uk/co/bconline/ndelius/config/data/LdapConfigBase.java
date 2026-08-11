package uk.co.bconline.ndelius.config.data;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ldap.autoconfigure.LdapConnectionDetails;
import org.springframework.boot.ldap.autoconfigure.LdapProperties;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;

import java.util.Collections;
import java.util.Locale;

public class LdapConfigBase {

    LdapContextSource ldapContextSource(LdapConnectionDetails connectionDetails, LdapProperties properties,
                                        ObjectProvider<DirContextAuthenticationStrategy> dirContextAuthenticationStrategy) {
        LdapContextSource source = new LdapContextSource();
        dirContextAuthenticationStrategy.ifUnique(source::setAuthenticationStrategy);
        PropertyMapper propertyMapper = PropertyMapper.get();
        propertyMapper.from(connectionDetails.getUsername()).to(source::setUserDn);
        propertyMapper.from(connectionDetails.getPassword()).to(source::setPassword);
        propertyMapper.from(properties.getAnonymousReadOnly()).to(source::setAnonymousReadOnly);
        propertyMapper.from(properties.getReferral())
            .as(((referral) -> referral.name().toLowerCase(Locale.ROOT)))
            .to(source::setReferral);
        propertyMapper.from(connectionDetails.getBase()).to(source::setBase);
        propertyMapper.from(connectionDetails.getUrls()).to(source::setUrls);
        propertyMapper.from(properties.getBaseEnvironment())
            .to((baseEnvironment) -> source.setBaseEnvironmentProperties(Collections.unmodifiableMap(baseEnvironment)));
        return source;
    }

    LdapTemplate ldapTemplate(LdapProperties properties, ContextSource contextSource,
                              ObjectDirectoryMapper objectDirectoryMapper) {
        LdapProperties.Template template = properties.getTemplate();
        PropertyMapper propertyMapper = PropertyMapper.get();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        ldapTemplate.setObjectDirectoryMapper(objectDirectoryMapper);
        propertyMapper.from(template.isIgnorePartialResultException())
            .to(ldapTemplate::setIgnorePartialResultException);
        propertyMapper.from(template.isIgnoreNameNotFoundException()).to(ldapTemplate::setIgnoreNameNotFoundException);
        propertyMapper.from(template.isIgnoreSizeLimitExceededException())
            .to(ldapTemplate::setIgnoreSizeLimitExceededException);
        return ldapTemplate;
    }

}
