package uk.co.bconline.ndelius.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.service.OIDUserService;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController
{
	@Value("${jwt.expiry}")
	private int expiry;

	private final OIDUserService oidUserService;

	@Autowired
	public LoginController(OIDUserService oidUserService)
	{
		this.oidUserService = oidUserService;
	}

	@Data
	@AllArgsConstructor
	private class TokenResponse
	{
		private String token;
		private int expiresIn;
	}

	@RequestMapping("/whoami")
	public Optional<OIDUser> whoami()
	{
		 val username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

		 return oidUserService.getUser(username).map(oidUser -> {
			 oidUser.setRoles(oidUserService.getUserRoles(oidUser.getUsername()));
			 return oidUser;
		 });
	}

	@PostMapping("/login")
	public TokenResponse login()
	{
		val authentication = SecurityContextHolder.getContext().getAuthentication();
		return new TokenResponse((String) authentication.getCredentials(), expiry);
	}
}