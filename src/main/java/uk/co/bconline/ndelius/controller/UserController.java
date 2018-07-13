package uk.co.bconline.ndelius.controller;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserService;
import uk.co.bconline.ndelius.service.impl.DBUserDetailsService;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController
{
	private final DBUserDetailsService dbUserDetailsService;
	private final UserService userService;

	@Autowired
	public UserController(DBUserDetailsService dbUserDetailsService, UserService userService)
	{
		this.dbUserDetailsService = dbUserDetailsService;
		this.userService = userService;
	}

	@Interaction("UMBI001")
	@GetMapping("/users")
	public ResponseEntity<List<SearchResult>> search(
			@RequestParam("q") String query,
			@Min(1) @RequestParam(value = "page", defaultValue = "1") Integer page,
			@Min(1) @Max(100) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
	{
		return new ResponseEntity<>(dbUserDetailsService.search(query, page, pageSize), HttpStatus.OK);
	}

	@Interaction("UMBI002")
	@GetMapping(path="/user/{username}")
	public ResponseEntity<User> getUser(final @PathVariable("username") String username)
	{
		return userService.getUser(username)
				.map(u -> new ResponseEntity<>(u, OK))
				.orElse(new ResponseEntity<>(NOT_FOUND));
	}
}