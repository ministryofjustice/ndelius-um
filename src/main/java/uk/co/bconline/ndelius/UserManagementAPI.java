package uk.co.bconline.ndelius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication(exclude = EmbeddedLdapAutoConfiguration.class)
public class UserManagementAPI {
	public static void main(String[] args) {
		SpringApplication.run(UserManagementAPI.class, args);
	}
}
