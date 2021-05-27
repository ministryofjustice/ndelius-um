package uk.co.bconline.ndelius.controller;

import com.google.common.collect.ImmutableMap;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

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
			// search terms
			@RequestParam("q") String query,
			// filters
			@RequestParam(value = "reportingGroup", defaultValue = "") Set<String> reportingGroups,
			@RequestParam(value = "fileshareGroup", defaultValue = "") Set<String> fileshareGroups,
			@RequestParam(value = "dataset", defaultValue = "") Set<String> datasets,
			@RequestParam(value = "includeInactiveUsers", defaultValue = "false") Boolean includeInactiveUsers,
			// paging
			@RequestParam(value = "page", defaultValue = "1") @Min(1)  Integer page,
			@RequestParam(value = "pageSize", defaultValue = "50") @Min(1) @Max(100) Integer pageSize)
	{
		val groups = ImmutableMap.<String, Set<String>>of("NDMIS-Reporting", reportingGroups, "Fileshare", fileshareGroups);
		return ok(userService.search(query, groups, datasets, includeInactiveUsers, page, pageSize));
	}

	@GetMapping("/users/export")
	@PreAuthorize("#oauth2.hasScope('UMBI001')")
	public void export(
			HttpServletResponse response,
			// search terms
			@RequestParam("q") String query,
			// filters
			@RequestParam(value = "reportingGroup", defaultValue = "") Set<String> reportingGroups,
			@RequestParam(value = "fileshareGroup", defaultValue = "") Set<String> fileshareGroups,
			@RequestParam(value = "dataset", defaultValue = "") Set<String> datasets,
			@RequestParam(value = "includeInactiveUsers", defaultValue = "false") Boolean includeInactiveUsers) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException
	{
		val groups = ImmutableMap.<String, Set<String>>of("NDMIS-Reporting", reportingGroups, "Fileshare", fileshareGroups);
		response.setContentType("text/csv");
		userService.exportSearchToCSV(query, groups, datasets, includeInactiveUsers,response.getWriter());
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