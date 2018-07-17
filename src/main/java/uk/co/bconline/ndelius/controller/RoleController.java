package uk.co.bconline.ndelius.controller;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.Transaction;
import uk.co.bconline.ndelius.model.ldap.OIDBusinessTransaction;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.service.impl.DBUserDetailsService;
import uk.co.bconline.ndelius.service.impl.RoleServiceImpl;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleController
{
	private final RoleService roleService;

	@Autowired
	public RoleController(RoleService roleService)
	{
		this.roleService = roleService;
	}

	@Interaction("UMBI007")
	@GetMapping(path="/roles")
	public ResponseEntity<Iterable<Transaction>> getRoles()
	{
		val roles = roleService.getRoles();

		return roles.iterator().hasNext() ? new ResponseEntity<>(roles, OK) : new ResponseEntity<>(NOT_FOUND);
	}
}