package uk.co.bconline.ndelius.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController
{
	@Value("${jwt.expiry}")
	private int expiry;

	@Data
	@AllArgsConstructor
	private class TokenResponse
	{
		private String token;
		private int expiresIn;
	}

	@RequestMapping("/whoami")
	public UserDetails whoami()
	{
		return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@RequestMapping("/login")
	public TokenResponse login()
	{
		val authentication = SecurityContextHolder.getContext().getAuthentication();
		return new TokenResponse((String) authentication.getCredentials(), expiry);
	}
}