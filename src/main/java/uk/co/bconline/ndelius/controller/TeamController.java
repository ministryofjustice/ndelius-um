package uk.co.bconline.ndelius.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.model.Team;
import uk.co.bconline.ndelius.service.TeamService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class TeamController
{
	private final TeamService teamService;

	@Autowired
	public TeamController(TeamService teamService)
	{
		this.teamService = teamService;
	}

	@GetMapping("/teams")
	@PreAuthorize("hasAuthority('SCOPE_UMBI009')")
	public ResponseEntity<List<Team>> getTeams(final @RequestParam(value = "provider", required = false) String provider)
	{
		return new ResponseEntity<>(teamService.getTeams(provider), HttpStatus.OK);
	}
}
