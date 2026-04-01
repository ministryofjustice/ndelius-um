package uk.co.bconline.ndelius.config.security;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerJdbcConfig {
    @Bean(name = "authorizationServerDataSourceProperties", defaultCandidate = false)
    @ConfigurationProperties("spring.datasource.auth")
    public DataSourceProperties authorizationServerDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "authorizationServerDataSource", defaultCandidate = false)
    @ConfigurationProperties("spring.datasource.auth.hikari")
    public HikariDataSource authorizationServerDataSource(
        @Qualifier("authorizationServerDataSourceProperties") DataSourceProperties properties
    ) {
        return properties.initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
    }

    @Bean(name = "authorizationServerJdbcOperations")
    @DependsOn("authorizationServerFlywayMigrator")
    public JdbcOperations authorizationServerJdbcOperations(@Qualifier("authorizationServerDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(
        @Qualifier("authorizationServerJdbcOperations") JdbcOperations jdbcOperations,
        RegisteredClientRepository registeredClientRepository
    ) {
        return new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(
        @Qualifier("authorizationServerJdbcOperations") JdbcOperations jdbcOperations,
        RegisteredClientRepository registeredClientRepository
    ) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcOperations, registeredClientRepository);
    }

    @Bean(name = "authorizationServerFlywayMigrator")
    public InitializingBean authorizationServerFlywayMigrator(@Qualifier("authorizationServerDataSource") DataSource dataSource) {
        return () -> Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/auth/migration")
            .load()
            .migrate();
    }
}
