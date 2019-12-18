package uk.co.bconline.ndelius.controller;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserEntryService;
import uk.co.bconline.ndelius.transformer.UserTransformer;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.noContent;
import static uk.co.bconline.ndelius.util.AuthUtils.myToken;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController
{
	private final ConsumerTokenServices tokenServices;
	private final UserEntryService userEntryService;
	private final UserTransformer transformer;

	@Autowired
	public LoginController(
			UserEntryService userEntryService,
			UserTransformer transformer,
			@Qualifier("consumerTokenServices") ConsumerTokenServices tokenServices)
	{
		this.userEntryService = userEntryService;
		this.transformer = transformer;
		this.tokenServices = tokenServices;
	}

	@GetMapping("/whoami")
	public Optional<User> whoami()
	{
		val username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userEntryService.getUser(username).flatMap(transformer::map);
	}

	@PostMapping("/logout")
	public ResponseEntity revokeToken()
	{
		tokenServices.revokeToken(myToken());
		return noContent().build();
	}
}