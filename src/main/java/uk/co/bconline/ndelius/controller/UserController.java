package uk.co.bconline.ndelius.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import uk.co.bconline.ndelius.model.User;

@Slf4j
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController
{
	@RequestMapping("/users")
	public User[] search(
			@RequestParam("q") String query,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
	{
		log.debug("Search q={}, page={}, pageSize={}", query, page, pageSize);

		// Here temporary for some quick UI development:
		return new User[]{
				User.builder().id("joe.bloggs").forenames("Joe").surname("Bloggs").build(),
				User.builder().id("jane.bloggs").forenames("Jane").surname("Bloggs").build(),
				User.builder().id("joe.smith").forenames("Joe").surname("Smith").build()
		};
	}
}