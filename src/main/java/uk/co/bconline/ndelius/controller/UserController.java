package uk.co.bconline.ndelius.controller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.OIDUser;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.OIDUserService;
import uk.co.bconline.ndelius.service.UserService;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController
{
	private final OIDUserService oidUserService;
	private final UserService userService;

	@Autowired
	public UserController(OIDUserService oidUserService, UserService userService)
	{
		this.oidUserService = oidUserService;
		this.userService = userService;
	}

	@Interaction("UMBI001")
	@RequestMapping("/users")
	public ResponseEntity<Iterable<OIDUser>> search(
			@RequestParam("q") String query,
			@Min(1) @RequestParam(value = "page", defaultValue = "1") Integer page,
			@Min(1) @Max(100) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
	{
		return new ResponseEntity<>(oidUserService.search(query, page, pageSize), HttpStatus.OK);
	}

	@Interaction("UMBI002")
	@RequestMapping(path="/user/{username}", method=RequestMethod.GET)
	public ResponseEntity<User> getUser(
			final @RequestHeader HttpHeaders httpHeaders, final @PathVariable("username") String username)
	{
		return userService.getUser(username).map(u -> new ResponseEntity(u, OK)).orElse(new ResponseEntity(NOT_FOUND));
	}
}