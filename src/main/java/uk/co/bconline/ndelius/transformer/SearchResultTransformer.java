package uk.co.bconline.ndelius.transformer;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.SearchResultEntity;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.entry.UserEntry;

import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static uk.co.bconline.ndelius.util.LdapUtils.mapLdapStringToDate;
import static uk.co.bconline.ndelius.util.NameUtils.combineNames;

@Slf4j
@Component
public class SearchResultTransformer
{
	@Value("${spring.ldap.useOracleAttributes:#{true}}")
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

	public SearchResult map(UserEntry user, float score) {
		return SearchResult.builder()
				.username(user.getUsername())
				.forenames(user.getForenames())
				.surname(user.getSurname())
				.email(user.getEmail())
				.endDate(mapLdapStringToDate(useOracleAttributes? user.getOracleEndDate(): user.getEndDate()))
				.sources(singletonList("LDAP"))
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

	public SearchResult map(UserEntity entity) {
		return SearchResult.builder()
				.username(entity.getUsername())
				.forenames(combineNames(entity.getForename(), entity.getForename2()))
				.surname(entity.getSurname())
				.endDate(entity.getEndDate())
				.staffCode(ofNullable(entity.getStaff()).map(StaffEntity::getCode).orElse(null))
				.teams(ofNullable(entity.getStaff()).map(StaffEntity::getTeams).map(teamTransformer::map).orElse(null))
				.sources(singletonList("DB"))
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
				.email(ofNullable(a.getEmail()).orElseGet(b::getEmail))
				.sources(Stream.concat(a.getSources().stream(), b.getSources().stream()).collect(toList()))
				.build();
	}

	public SearchResultEntity reduceTeams(SearchResultEntity a, SearchResultEntity b)
	{
		val reduced = a.toBuilder()
				.score(Math.max(a.getScore(), b.getScore()))
				.build();
		reduced.getTeams().addAll(a.getTeams());
		reduced.getTeams().addAll(b.getTeams());
		return reduced;
	}

	public SearchResultEntity reduce(SearchResultEntity a, SearchResultEntity b)
	{
		return a.withScore(a.getScore() + b.getScore());
	}
}
