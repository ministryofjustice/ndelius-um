package uk.co.bconline.ndelius.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserService;
import uk.co.bconline.ndelius.validator.NewUsernameMustNotAlreadyExist;
import uk.co.bconline.ndelius.validator.UsernameMustNotAlreadyExist;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.springframework.http.ResponseEntity.*;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController
{
	private final UserService userService;

	@Autowired
	public UserController(UserService userService)
	{
		this.userService = userService;
	}

	@Interaction("UMBI001")
	@GetMapping("/users")
	public ResponseEntity<List<SearchResult>> search(
			@RequestParam("q") String query,
			@Min(1) @RequestParam(value = "page", defaultValue = "1") Integer page,
			@Min(1) @Max(100) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
	{
		return ok(userService.search(query, page, pageSize));
	}

	@Interaction("UMBI002")
	@GetMapping(path="/user/{username}")
	public ResponseEntity<User> getUser(@PathVariable("username") String username)
	{
		return userService.getUser(username)
				.map(ResponseEntity::ok)
				.orElse(notFound().build());
	}

	@Interaction("UMBI002")
	@GetMapping(path="/staff/{staffCode}")
	public ResponseEntity<User> getUserByStaffCode(@PathVariable("staffCode") String staffCode)
	{
		return userService.getUserByStaffCode(staffCode)
				.map(ResponseEntity::ok)
				.orElse(notFound().build());
	}

	@Transactional
	@Interaction("UMBI003")
	@PostMapping(path="/user")
	@UsernameMustNotAlreadyExist
	public ResponseEntity addUser(@RequestBody @Valid User user) throws URISyntaxException
	{
		userService.addUser(user);
		return created(new URI(String.format("/user/%s", user.getUsername()))).build();
	}

	@Transactional
	@Interaction("UMBI004")
	@PostMapping(path="/user/{username}")
	@NewUsernameMustNotAlreadyExist
	public ResponseEntity updateUser(@RequestBody @Valid User user, @PathVariable("username") String username)
	{
		if (!userService.usernameExists(username))
		{
			return notFound().build();
		}
		else
		{
			userService.updateUser(username, user);
			return noContent().build();
		}
	}
}