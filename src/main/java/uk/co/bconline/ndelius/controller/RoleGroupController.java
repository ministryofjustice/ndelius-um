package uk.co.bconline.ndelius.controller;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.RoleGroup;
import uk.co.bconline.ndelius.service.UserRoleGroupService;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleGroupController
{
	private final UserRoleGroupService roleGroupService;

	@Autowired
	public RoleGroupController(UserRoleGroupService roleGroupService)
	{
		this.roleGroupService = roleGroupService;
	}

	@Interaction("UMBI007")
	@GetMapping(path="/rolegroups")
	public ResponseEntity<List<RoleGroup>> getRoleGroups()
	{
		val roleGroups = roleGroupService.getAssignableRoleGroups();
		return roleGroups.iterator().hasNext() ? new ResponseEntity<>(roleGroups, OK) : new ResponseEntity<>(NOT_FOUND);
	}

	@Interaction("UMBI007")
	@GetMapping(path="/rolegroup/{rolegroupname}")
	public ResponseEntity<RoleGroup> getRoleGroup(final @PathVariable("rolegroupname") String roleGroupName)
	{
		return roleGroupService.getRoleGroup(roleGroupName)
				.map(rg -> new ResponseEntity<>(rg, OK))
				.orElse(new ResponseEntity<>(NOT_FOUND));
	}
}