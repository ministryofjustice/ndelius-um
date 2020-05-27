package uk.co.bconline.ndelius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.redis.RedisReactiveHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication(exclude = {EmbeddedLdapAutoConfiguration.class, RedisReactiveHealthContributorAutoConfiguration.class})
// RedisReactiveHealthContributorAutoConfiguration has been excluded above due to a bug in Spring Boot 2.3.0 when
// working with clustered Redis (https://github.com/spring-projects/spring-boot/issues/21514). This should be removed
// once a fix is applied in Spring Boot.
public class UserManagementAPI {
	public static void main(String[] args) {
		SpringApplication.run(UserManagementAPI.class, args);
	}
}
