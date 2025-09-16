package uk.co.bconline.ndelius.controller;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserEntryService;
import uk.co.bconline.ndelius.service.impl.ClientEntryServiceImpl;
import uk.co.bconline.ndelius.transformer.UserTransformer;
import uk.co.bconline.ndelius.util.AuthUtils;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController
{
	private final UserEntryService userEntryService;
	private final ClientEntryServiceImpl clientEntryService;
	private final UserTransformer transformer;
//	private final ConsumerTokenServices tokenServices;

	@Autowired
	public LoginController(
			UserEntryService userEntryService,
			ClientEntryServiceImpl clientEntryService,
			UserTransformer transformer
//			@Qualifier("consumerTokenServices") ConsumerTokenServices tokenServices
	) {
		this.userEntryService = userEntryService;
		this.clientEntryService = clientEntryService;
		this.transformer = transformer;
//		this.tokenServices = tokenServices;
	}

	@GetMapping("/whoami")
	public User whoami()
	{
		val username = AuthUtils.myUsername();
		val user = AuthUtils.isClient()?
			clientEntryService.getClient(username).flatMap(transformer::map):
			userEntryService.getUser(username).flatMap(transformer::map);

		return user.orElseGet(() -> User.builder().username(username).build());
	}

	// TODO revoke token?
//	@PostMapping("/logout")
//	public ResponseEntity revokeToken()
//	{
//		tokenServices.revokeToken(myToken());
//		return noContent().build();
//	}
}