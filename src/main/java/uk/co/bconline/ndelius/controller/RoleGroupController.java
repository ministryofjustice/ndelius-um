package uk.co.bconline.ndelius.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.TransactionGroup;
import uk.co.bconline.ndelius.service.RoleGroupService;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleGroupController
{
	private final RoleGroupService roleGroupService;

	@Autowired
	public RoleGroupController(RoleGroupService roleGroupService)
	{
		this.roleGroupService = roleGroupService;
	}

	@Interaction("UMBI007")
	@GetMapping(path="/rolegroups")
	public ResponseEntity<Iterable<TransactionGroup>> getRoleGroups()
	{
		val roleGroups = roleGroupService.getTransactionGroups();
		return roleGroups.iterator().hasNext() ? new ResponseEntity<>(roleGroups, OK) : new ResponseEntity<>(NOT_FOUND);
	}

	@Interaction("UMBI007")
	@GetMapping(path="/rolegroup/{rolegroupname}")
	public ResponseEntity<TransactionGroup> getRoleGroup(final @PathVariable("rolegroupname") String roleGroupName)
	{
		return roleGroupService.getTransactionGroup(roleGroupName)
				.map(rg -> new ResponseEntity<>(rg, OK))
				.orElse(new ResponseEntity<>(NOT_FOUND));
	}
}