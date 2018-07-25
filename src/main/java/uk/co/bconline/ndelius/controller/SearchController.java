package uk.co.bconline.ndelius.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.service.SearchService;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchController
{
	private final SearchService service;

	@Autowired
	public SearchController(SearchService service)
	{
		this.service = service;
	}

	@Interaction("UMBI010")
	@PostMapping(path="/search/reindex")
	public ResponseEntity reindex()
	{
		service.reindex();
		return ResponseEntity.ok().build();
	}

}