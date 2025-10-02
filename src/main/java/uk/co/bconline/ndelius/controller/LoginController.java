package uk.co.bconline.ndelius.controller;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserEntryService;
import uk.co.bconline.ndelius.service.impl.ClientEntryServiceImpl;
import uk.co.bconline.ndelius.transformer.UserTransformer;
import uk.co.bconline.ndelius.util.AuthUtils;

import static java.util.Optional.ofNullable;
import static org.springframework.http.ResponseEntity.noContent;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {
    private final UserEntryService userEntryService;
    private final ClientEntryServiceImpl clientEntryService;
    private final UserTransformer transformer;
    private final OAuth2AuthorizationService authorizationService;

    @Autowired
    public LoginController(
        UserEntryService userEntryService,
        ClientEntryServiceImpl clientEntryService,
        UserTransformer transformer,
        OAuth2AuthorizationService authorizationService
    ) {
        this.userEntryService = userEntryService;
        this.clientEntryService = clientEntryService;
        this.transformer = transformer;
        this.authorizationService = authorizationService;
    }

    @GetMapping("/whoami")
    public User whoami() {
        val username = AuthUtils.myUsername();
        val user = AuthUtils.isClient() ?
            clientEntryService.getClient(username).flatMap(transformer::map) :
            userEntryService.getUser(username).flatMap(transformer::map);

        return user.orElseGet(() -> User.builder().username(username).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> revokeToken() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof BearerTokenAuthentication authentication) {
            val token = ofNullable(authentication.getToken()).map(OAuth2AccessToken::getTokenValue).orElse(null);
            if (token != null) {
                ofNullable(authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN)).ifPresent(authorizationService::remove);
                ofNullable(authorizationService.findByToken(token, OAuth2TokenType.REFRESH_TOKEN)).ifPresent(authorizationService::remove);
            }
        }
        return noContent().build();
    }
}
