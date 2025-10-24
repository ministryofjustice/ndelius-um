package uk.co.bconline.ndelius;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@OpenAPIDefinition(
    info = @Info(
        title = "Delius User Management Tool (UMT)",
        description = "REST API to enable NPS-ICT to effectively create and maintain user accounts in the National Delius application, and to allow external services to securely authenticate Probation staff.",
        contact = @Contact(name = "Unilink", url = "https://unilink.com")),
    externalDocs = @ExternalDocumentation(description = "GitHub", url = "https://github.com/ministryofjustice/ndelius-um"))
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication(exclude = {EmbeddedLdapAutoConfiguration.class})
public class UserManagementAPI {
    public static void main(String[] args) {
        SpringApplication.run(UserManagementAPI.class, args);
    }
}
