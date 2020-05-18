package uk.co.bconline.ndelius.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.model.UserHistoryItem;
import uk.co.bconline.ndelius.service.UserHistoryService;

import java.util.List;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserHistoryController
{
	private final UserHistoryService userHistoryService;

	@Autowired
	public UserHistoryController(UserHistoryService userHistoryService) {
		this.userHistoryService = userHistoryService;
	}

	@GetMapping(path="/user/{username}/history")
	@PreAuthorize("#oauth2.hasScope('UMBI002')")
	public ResponseEntity<List<UserHistoryItem>> getUserHistory(@PathVariable("username") String username) {
		try {
			return ok(this.userHistoryService.getHistory(username));
		} catch (UsernameNotFoundException e) {
			return notFound().build();
		}
	}
}