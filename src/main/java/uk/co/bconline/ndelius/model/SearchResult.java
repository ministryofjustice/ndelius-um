package uk.co.bconline.ndelius.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
public final class SearchResult
{
	private String username;
	private String aliasUsername;
	private String forenames;
	private String surname;
	private List<Team> teams;
	private String staffCode;
	private List<String> sources;
	@JsonIgnore
	private float score;
}