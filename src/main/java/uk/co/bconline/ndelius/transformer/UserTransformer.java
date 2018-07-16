package uk.co.bconline.ndelius.transformer;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.Team;
import uk.co.bconline.ndelius.model.Transaction;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.TeamEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.model.ldap.OIDBusinessTransaction;
import uk.co.bconline.ndelius.model.ldap.OIDUser;

@Component
public class UserTransformer
{
	private final DatasetTransformer datasetTransformer;

	@Autowired
	public UserTransformer(DatasetTransformer datasetTransformer)
	{
		this.datasetTransformer = datasetTransformer;
	}

	public SearchResult map(UserEntity user)
	{
		return SearchResult.builder()
				.username(user.getUsername())
				.forenames(combineForenames(user.getForename(), user.getForename2()))
				.surname(user.getSurname())
				.staffCode(ofNullable(user.getStaff()).map(StaffEntity::getCode).orElse(null))
				.build();
	}

	public Transaction map(OIDBusinessTransaction transaction){
		return Transaction.builder()
				.name(transaction.getName())
				.roles(transaction.getRoles())
				.description(transaction.getDescription())
				.build();
	}

	public Optional<User> map(OIDUser user)
	{
		return combine(null, user, null, null);
	}

	public Optional<User> combine(UserEntity dbUser, OIDUser oidUser, ADUser ad1User, ADUser ad2User)
	{
		return Optional.of(ofNullable(ad2User)
				.map(v -> User.builder()
						// AD2 details
						.username(v.getUsername())
						.build()).orElse(new User()))
				.map(u -> ofNullable(ad1User).map(v -> u.toBuilder()
						// AD1 details
						.username(v.getUsername())
						.build()).orElse(u))
				.map(u -> ofNullable(dbUser).map(v -> u.toBuilder()
						// DB details
						.username(v.getUsername())
						.datasets(datasetTransformer.map(v.getDatasets()))
						.organisation(datasetTransformer.map(v.getOrganisation()))
						.staffCode(ofNullable(v.getStaff()).map(StaffEntity::getCode).orElse(null))
						.teams(ofNullable(v.getStaff()).map(StaffEntity::getTeam).map(this::map).orElse(null))
						.endDate(v.getEndDate())
						.build()).orElse(u))
				.map(u -> ofNullable(oidUser).map(v -> u.toBuilder()
						// OID details
						.username(v.getUsername())
						.forenames(v.getForenames())
						.surname(v.getSurname())
						.transactions(ofNullable(v.getTransactions())
								.map(transactions -> transactions.stream()
										.map(this::map)
										.collect(toList()))
								.orElse(null))
						.build()).orElse(u));
	}

	private List<Team> map(Collection<TeamEntity> teams)
	{
		return teams.stream()
				.map(team -> Team.builder()
						.code(team.getCode())
						.description(team.getDescription())
						.build())
				.collect(toList());
	}

	private String combineForenames(String forename, String forename2)
	{
		if (isEmpty(forename)) return "";
		if (isEmpty(forename2)) return forename;
		return forename + " " + forename2;
	}
}
