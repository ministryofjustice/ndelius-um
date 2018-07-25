package uk.co.bconline.ndelius.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public final class SearchResult
{
	private String username;
	private String aliasUsername;
	private String forenames;
	private String surname;
	private List<Team> teams;
	private String staffCode;

	private boolean inNationalDelius;
	private boolean inPrimaryAD;
	private boolean inSecondaryAD;
}