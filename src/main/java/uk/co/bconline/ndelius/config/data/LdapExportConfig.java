package uk.co.bconline.ndelius.config.data;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ldap.autoconfigure.LdapConnectionDetails;
import org.springframework.boot.ldap.autoconfigure.LdapProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;

@Configuration
public class LdapExportConfig {
    @Value("${spring.ldap.export.username:${spring.ldap.username}}")
    private String exportUser;

    @Bean("exportLdapContextSource")
    public LdapContextSource ldapContextSource(LdapConnectionDetails connectionDetails, LdapProperties properties,
                                               ObjectProvider<DirContextAuthenticationStrategy> dirContextAuthenticationStrategy) {
        return LdapBeanFactory.contextSource(connectionDetails, properties, dirContextAuthenticationStrategy, exportUser);
    }

    @Bean("exportLdapTemplate")
    public LdapTemplate ldapTemplate(LdapProperties properties,
                                     @Qualifier("exportLdapContextSource") ContextSource contextSource,
                                     ObjectDirectoryMapper objectDirectoryMapper) {
        return LdapBeanFactory.template(properties, contextSource, objectDirectoryMapper);
    }
}
