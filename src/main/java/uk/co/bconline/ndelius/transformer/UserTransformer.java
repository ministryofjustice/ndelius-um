package uk.co.bconline.ndelius.transformer;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static uk.co.bconline.ndelius.util.NameUtils.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.val;
import uk.co.bconline.ndelius.model.*;
import uk.co.bconline.ndelius.model.entity.*;
import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.service.DatasetService;
import uk.co.bconline.ndelius.service.OrganisationService;
import uk.co.bconline.ndelius.service.ReferenceDataService;
import uk.co.bconline.ndelius.service.TeamService;
import uk.co.bconline.ndelius.service.impl.AD1UserDetailsService;
import uk.co.bconline.ndelius.service.impl.AD2UserDetailsService;

@Component
public class UserTransformer
{
	private final TeamService teamService;
	private final ReferenceDataService referenceDataService;
	private final DatasetService datasetService;
	private final OrganisationService organisationService;
	private final Optional<AD1UserDetailsService> ad1UserDetailsService;
	private final Optional<AD2UserDetailsService> ad2UserDetailsService;
	private final DatasetTransformer datasetTransformer;
	private final ReferenceDataTransformer referenceDataTransformer;

	@Autowired
	public UserTransformer(
			TeamService teamService,
			ReferenceDataService referenceDataService,
			DatasetService datasetService,
			OrganisationService organisationService,
			Optional<AD1UserDetailsService> ad1UserDetailsService,
			Optional<AD2UserDetailsService> ad2UserDetailsService,
			DatasetTransformer datasetTransformer,
			ReferenceDataTransformer referenceDataTransformer)
	{
		this.teamService = teamService;
		this.referenceDataService = referenceDataService;
		this.datasetService = datasetService;
		this.organisationService = organisationService;
		this.ad1UserDetailsService = ad1UserDetailsService;
		this.ad2UserDetailsService = ad2UserDetailsService;
		this.datasetTransformer = datasetTransformer;
		this.referenceDataTransformer = referenceDataTransformer;
	}

	public SearchResult map(User user)
	{
		return SearchResult.builder()
				.username(user.getUsername())
				.aliasUsername(user.getAliasUsername())
				.forenames(user.getForenames())
				.surname(user.getSurname())
				.teams(user.getTeams())
				.staffCode(user.getStaffCode())
				.inNationalDelius(true)
				.inPrimaryAD(ad1UserDetailsService.flatMap(service -> service.getUser(user.getUsername())).isPresent() ||
						ad1UserDetailsService.flatMap(service -> service.getUser(user.getAliasUsername())).isPresent())
				.inSecondaryAD(ad2UserDetailsService.flatMap(service -> service.getUser(user.getUsername())).isPresent())
				.build();
	}

	public Role map(OIDRole oidRole)
	{
		return Role.builder()
				.name(oidRole.getName())
				.description(oidRole.getDescription())
				.interactions(oidRole.getName().startsWith("UMBT")? oidRole.getInteractions(): null)
				.build();
	}

	public OIDRole map(Role role)
	{
		return OIDRole.builder()
				.name(role.getName())
				.description(role.getDescription())
				.build();
	}

	public Optional<User> map(OIDUser user)
	{
		return combine(null, user, null, null);
	}

	public Optional<User> combine(UserEntity dbUser, OIDUser oidUser, ADUser ad1User, ADUser ad2User)
	{
		return Stream.of(
				ofNullable(oidUser).map(v -> User.builder()
						.username(v.getUsername())
						.aliasUsername(v.getAliasUsername())
						.forenames(v.getForenames())
						.surname(v.getSurname())
						.homeArea(datasetService.getDatasetByCode(v.getHomeArea())
								.orElse(Dataset.builder().code(v.getHomeArea()).build()))
						.roles(ofNullable(v.getRoles())
								.map(transactions -> transactions.stream()
										.map(this::map)
										.collect(toList()))
								.orElse(null))
						.build()),
				ofNullable(dbUser).map(v -> User.builder()
						.username(v.getUsername())
						.forenames(combineForenames(v.getForename(), v.getForename2()))
						.surname(v.getSurname())
						.datasets(datasetTransformer.map(v.getDatasets()))
						.organisation(datasetTransformer.map(v.getOrganisation()))
						.staffCode(ofNullable(v.getStaff()).map(StaffEntity::getCode).orElse(null))
						.staffGrade(ofNullable(v.getStaff())
								.map(StaffEntity::getGrade)
								.map(referenceDataTransformer::map)
								.orElse(null))
						.startDate(ofNullable(v.getStaff()).map(StaffEntity::getStartDate).orElse(null))
						.endDate(ofNullable(v.getStaff()).map(StaffEntity::getEndDate).filter(Objects::nonNull).orElse(v.getEndDate()))
						.teams(ofNullable(v.getStaff()).map(StaffEntity::getTeams).map(this::map).orElse(null))
						.build()),
				ofNullable(ad1User).map(v -> User.builder()
						.username(v.getUsername())
						.build()),
				ofNullable(ad2User).map(v -> User.builder()
						.username(v.getUsername())
						.build()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.reduce(this::reduceUser);
	}

	private User reduceUser(User a, User b)
	{
		return a.toBuilder()
				.username(ofNullable(a.getUsername()).orElse(b.getUsername()))
				.forenames(ofNullable(a.getForenames()).orElse(b.getForenames()))
				.surname(ofNullable(a.getSurname()).orElse(b.getSurname()))
				.staffCode(ofNullable(a.getStaffCode()).orElse(b.getStaffCode()))
				.staffGrade(ofNullable(a.getStaffGrade()).orElse(b.getStaffGrade()))
				.homeArea(ofNullable(a.getHomeArea()).orElse(b.getHomeArea()))
				.startDate(ofNullable(a.getStartDate()).orElse(b.getStartDate()))
				.endDate(ofNullable(a.getEndDate()).orElse(b.getEndDate()))
				.organisation(ofNullable(a.getOrganisation()).orElse(b.getOrganisation()))
				.teams(ofNullable(a.getTeams()).orElse(b.getTeams()))
				.datasets(ofNullable(a.getDatasets()).orElse(b.getDatasets()))
				.roles(ofNullable(a.getRoles()).orElse(b.getRoles()))
				.build();
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

	public UserEntity mapToUserEntity(User user, UserEntity existingUser)
	{
		val staff = mapToStaffEntity(user, ofNullable(existingUser.getStaff()).orElse(new StaffEntity()));
		val entity = existingUser.toBuilder()
				.username(user.getUsername())
				.forename(staff.getForename())
				.forename2(staff.getForename2())
				.surname(user.getSurname())
				.endDate(user.getEndDate())
				.organisation(ofNullable(user.getOrganisation())
						.map(Organisation::getCode)
						.flatMap(organisationService::getOrganisationId)
						.map(OrganisationEntity::new)
						.orElse(null))
				.datasets(ofNullable(user.getDatasets()).map(list -> list.stream()
						.map(dataset -> datasetService.getDatasetId(dataset.getCode())
								.map(ProbationAreaEntity::new)
								.orElse(null))
						.filter(Objects::nonNull)
						.collect(toSet()))
						.orElse(emptySet()))
				.staff(staff)
				.build();
		staff.setUser(entity);
		return entity;
	}

	public StaffEntity mapToStaffEntity(User user, StaffEntity existingStaff)
	{
		return existingStaff.toBuilder()
				.code(user.getStaffCode())
				.grade(ofNullable(user.getStaffGrade())
						.map(ReferenceData::getCode)
						.flatMap(referenceDataService::getStaffGradeId)
						.map(ReferenceDataEntity::new)
						.orElse(null))
				.forename(firstForename(user.getForenames()))
				.forename2(subsequentForenames(user.getForenames()))
				.surname(user.getSurname())
				.startDate(user.getStartDate())
				.endDate(user.getEndDate())
				.teams(ofNullable(user.getTeams()).map(list -> list.stream()
						.map(team -> teamService.getTeamId(team.getCode())
								.map(TeamEntity::new)
								.orElse(null))
						.filter(Objects::nonNull)
						.collect(toSet()))
						.orElse(emptySet()))
				.build();
	}

	public OIDUser mapToOIDUser(User user, OIDUser existingUser)
	{
		return existingUser.toBuilder()
				.username(user.getUsername())
				.aliasUsername(user.getAliasUsername())
				.forenames(user.getForenames())
				.surname(user.getSurname())
				.homeArea(ofNullable(user.getHomeArea()).map(Dataset::getCode).orElse(null))
				.roles(ofNullable(user.getRoles()).map(list -> list.stream()
						.map(this::map)
						.collect(toList()))
						.orElse(emptyList()))
				.build();
	}

	public ADUser mapToAD2User(User user, ADUser existingUser)
	{
		return existingUser.toBuilder()
				.username(user.getUsername())
				.build();
	}

	public ADUser mapToAD1User(User user, ADUser existingUser)
	{
		return existingUser.toBuilder()
				.username(ofNullable(user.getAliasUsername()).orElse(user.getUsername()))
				.build();
	}
}
