package uk.co.bconline.ndelius.controller;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.model.TokenResponse;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserEntryService;
import uk.co.bconline.ndelius.transformer.UserTransformer;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController
{
	@Value("${jwt.expiry}")
	private int expiry;

	private final UserEntryService userEntryService;
	private final UserTransformer transformer;

	@Autowired
	public LoginController(UserEntryService userEntryService, UserTransformer transformer)
	{
		this.userEntryService = userEntryService;
		this.transformer = transformer;
	}

	@GetMapping("/whoami")
	public Optional<User> whoami()
	{
		 val username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

		 return userEntryService.getUser(username).flatMap(transformer::map);
	}

	@PostMapping("/login")
	public TokenResponse login()
	{
		val authentication = SecurityContextHolder.getContext().getAuthentication();
		return new TokenResponse((String) authentication.getCredentials(), expiry);
	}
}