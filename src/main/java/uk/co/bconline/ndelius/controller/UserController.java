package uk.co.bconline.ndelius.controller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import uk.co.bconline.ndelius.model.OIDUser;
import uk.co.bconline.ndelius.service.OIDUserService;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController
{
	private final OIDUserService oidUserService;

	@Autowired
	public UserController(OIDUserService oidUserService)
	{
		this.oidUserService = oidUserService;
	}

	@RequestMapping("/users")
	public Iterable<OIDUser> search(
			@RequestParam("q") String query,
			@Min(1)
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@Min(1) @Max(100)
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
	{
		log.debug("Search q={}, page={}, pageSize={}", query, page, pageSize);

		return oidUserService.search(query, page, pageSize);
	}
}