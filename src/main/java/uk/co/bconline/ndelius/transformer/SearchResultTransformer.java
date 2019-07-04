package uk.co.bconline.ndelius.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.SearchResultEntity;
import uk.co.bconline.ndelius.model.entity.TeamEntity;
import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.model.ldap.OIDUser;

import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static uk.co.bconline.ndelius.util.LdapUtils.mapOIDStringToDate;
import static uk.co.bconline.ndelius.util.NameUtils.combineNames;

@Slf4j
@Component
public class SearchResultTransformer
{
	@Value("${oid.useOracleAttributes:#{true}}")
	private boolean useOracleAttributes;

	private final TeamTransformer teamTransformer;

	@Autowired
	public SearchResultTransformer(TeamTransformer teamTransformer) {
		this.teamTransformer = teamTransformer;
	}

	public SearchResult map(User user)
	{
		return SearchResult.builder()
				.username(user.getUsername())
				.forenames(user.getForenames())
				.surname(user.getSurname())
				.teams(user.getTeams())
				.staffCode(user.getStaffCode())
				.endDate(user.getEndDate())
				.sources(user.getSources())
				.build();
	}

	public SearchResult map(OIDUser user, float score) {
		return SearchResult.builder()
				.username(user.getUsername())
				.forenames(user.getForenames())
				.surname(user.getSurname())
				.endDate(mapOIDStringToDate(useOracleAttributes? user.getOracleEndDate(): user.getEndDate()))
				.sources(singletonList("OID"))
				.score(score)
				.build();
	}

	public SearchResult map(ADUser user, float score) {
		return SearchResult.builder()
				.username(user.getUsername())
				.forenames(user.getForename())
				.surname(user.getSurname())
				.sources(singletonList("AD?"))
				.score(score)
				.build();
	}

	public SearchResult map(SearchResultEntity entity) {
		return SearchResult.builder()
				.username(entity.getUsername())
				.forenames(combineNames(entity.getForename(), entity.getForename2()))
				.surname(entity.getSurname())
				.endDate(entity.getEndDate())
				.staffCode(entity.getStaffCode())
				.teams(teamTransformer.map(entity.getTeams()))
				.sources(singletonList("DB"))
				.score(entity.getScore())
				.build();
	}

	public SearchResult reduce(SearchResult a, SearchResult b)
	{
		return a.toBuilder()
				.username(ofNullable(a.getUsername()).orElseGet(b::getUsername))
				.forenames(ofNullable(a.getForenames()).orElseGet(b::getForenames))
				.surname(ofNullable(a.getSurname()).orElseGet(b::getSurname))
				.staffCode(ofNullable(a.getStaffCode()).orElseGet(b::getStaffCode))
				.teams(ofNullable(a.getTeams()).orElseGet(b::getTeams))
				.endDate(ofNullable(a.getEndDate()).orElseGet(b::getEndDate))
				.sources(Stream.concat(a.getSources().stream(), b.getSources().stream()).collect(toList()))
				.build();
	}

	public SearchResultEntity reduce(SearchResultEntity a, SearchResultEntity b)
	{
		if (a.getTeamCode() != null) a.getTeams().add(getTeam(a));
		if (b.getTeamCode() != null) b.getTeams().add(getTeam(b));
		return a.toBuilder()
				.teams(Stream.concat(a.getTeams().stream(), b.getTeams().stream()).collect(toSet()))
				.score(a.getScore() + b.getScore())
				.build();
	}

	public TeamEntity getTeam(SearchResultEntity entity)
	{
		return TeamEntity.builder()
				.code(entity.getTeamCode())
				.description(entity.getTeamDescription())
				.build();
	}
}