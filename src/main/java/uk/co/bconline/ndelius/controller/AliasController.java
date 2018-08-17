package uk.co.bconline.ndelius.controller;

import static org.springframework.http.ResponseEntity.*;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.Alias;
import uk.co.bconline.ndelius.model.ErrorResponse;
import uk.co.bconline.ndelius.service.impl.OIDUserDetailsService;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AliasController
{
	private final OIDUserDetailsService userService;

	@Autowired
	public AliasController(OIDUserDetailsService userService)
	{
		this.userService = userService;
	}

	@Interaction("UMBI004")
	@PostMapping(path="/alias/{username}")
	public ResponseEntity updateUser(
			@PathVariable("username") String username,
			@RequestBody @Valid Alias alias)
	{
		if (!username.equals(alias.getUsername())) return badRequest().body(new ErrorResponse("Username mismatch"));

		val user = userService.getUser(username);
		if (!user.isPresent()) return notFound().build();

		userService.save(user.get().toBuilder()
				.aliasUsername(alias.getAliasUsername()).build());

		return noContent().build();
	}
}