package uk.co.bconline.ndelius.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserService;
import uk.co.bconline.ndelius.validator.NewUsernameMustNotAlreadyExist;
import uk.co.bconline.ndelius.validator.UsernameMustNotAlreadyExist;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.springframework.http.ResponseEntity.*;
import static uk.co.bconline.ndelius.util.AuthUtils.myUsername;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController
{
	private final UserService userService;
	private final LoginController loginController;

	@Autowired
	public UserController(
			UserService userService,
			LoginController loginController)
	{
		this.userService = userService;
		this.loginController = loginController;
	}

	@GetMapping("/users")
	@PreAuthorize("#oauth2.hasScope('UMBI001')")
	public ResponseEntity<List<SearchResult>> search(
			@RequestParam("q") String query,
			@Min(1) @RequestParam(value = "page", defaultValue = "1") Integer page,
			@Min(1) @Max(100) @RequestParam(value = "pageSize", defaultValue = "50") Integer pageSize,
			@RequestParam(value = "includeInactiveUsers", defaultValue = "false") Boolean includeInactiveUsers)
	{
		return ok(userService.search(query, page, pageSize, includeInactiveUsers));
	}

	@GetMapping(path="/user/{username}")
	@PreAuthorize("#oauth2.hasScope('UMBI002')")
	public ResponseEntity<User> getUser(@PathVariable("username") String username)
	{
		return userService.getUser(username)
				.map(ResponseEntity::ok)
				.orElse(notFound().build());
	}

	@GetMapping(path="/staff/{staffCode}")
	@PreAuthorize("#oauth2.hasScope('UMBI002')")
	public ResponseEntity<User> getUserByStaffCode(@PathVariable("staffCode") String staffCode)
	{
		return userService.getUserByStaffCode(staffCode)
				.map(ResponseEntity::ok)
				.orElse(notFound().build());
	}

	@Transactional
	@PostMapping(path="/user")
	@UsernameMustNotAlreadyExist
	@PreAuthorize("#oauth2.hasScope('UMBI003')")
	public ResponseEntity addUser(@RequestBody User user) throws URISyntaxException
	{
		userService.addUser(user);
		return created(new URI(String.format("/user/%s", user.getUsername()))).build();
	}

	@Transactional
	@NewUsernameMustNotAlreadyExist
	@PostMapping(path="/user/{username}")
	@PreAuthorize("#oauth2.hasScope('UMBI004')")
	public ResponseEntity updateUser(@RequestBody User user, @PathVariable("username") String username)
	{
		if (!userService.usernameExists(username))
		{
			return notFound().build();
		}
		else
		{
			user.setExistingUsername(username);
			userService.updateUser(user);
			if (username.equals(myUsername()) && !username.equals(user.getUsername())) {
				log.debug("Username has changed! Revoking access token for {}", username);
				loginController.revokeToken();
			}
			return noContent().build();
		}
	}
}