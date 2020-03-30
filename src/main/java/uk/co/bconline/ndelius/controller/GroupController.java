package uk.co.bconline.ndelius.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.model.Group;
import uk.co.bconline.ndelius.service.GroupService;
import uk.co.bconline.ndelius.transformer.GroupTransformer;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupController {
	private final GroupService groupService;
	private final GroupTransformer groupTransformer;

	@Autowired
	public GroupController(
			GroupService groupService,
			GroupTransformer groupTransformer
	) {
		this.groupService = groupService;
		this.groupTransformer = groupTransformer;
	}

	@GetMapping("/groups")
	@PreAuthorize("#oauth2.hasScope('UMBI012')")
	public ResponseEntity<List<Group>> getGroups() {
		return ok(groupTransformer.map(groupService.getGroups()));
	}

	@GetMapping("/groups/{type}")
	@PreAuthorize("#oauth2.hasScope('UMBI012')")
	public ResponseEntity<List<Group>> getGroups(@PathVariable String type) {
		return ok(groupTransformer.map(groupService.getGroups(type)));
	}

	@GetMapping("/group/{name}")
	@PreAuthorize("#oauth2.hasScope('UMBI012')")
	public ResponseEntity<Group> getTopLevelGroup(@PathVariable String name) {
		return groupService.getGroup(name)
				.map(groupTransformer::mapWithMembers)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/group/{type}/{name}")
	@PreAuthorize("#oauth2.hasScope('UMBI012')")
	public ResponseEntity<Group> getGroupByTypeAndName(@PathVariable String type, @PathVariable String name) {
		return groupService.getGroup(type, name)
				.map(groupTransformer::mapWithMembers)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
}
