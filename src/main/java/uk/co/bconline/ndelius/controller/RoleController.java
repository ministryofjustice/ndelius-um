package uk.co.bconline.ndelius.controller;

import static java.util.stream.Collectors.toSet;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.transformer.RoleTransformer;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleController
{
	private final RoleService roleService;
	private final RoleTransformer roleTransformer;

	@Autowired
	public RoleController(
			RoleService roleService,
			RoleTransformer roleTransformer)
	{
		this.roleService = roleService;
		this.roleTransformer = roleTransformer;
	}

	@Interaction("UMBI007")
	@GetMapping(path="/roles")
	public ResponseEntity<Set<Role>> getRoles()
	{
		val roles = roleService.getAllRoles().stream().map(roleTransformer::map).collect(toSet());

		return !roles.isEmpty()? ok(roles): notFound().build();
	}
}