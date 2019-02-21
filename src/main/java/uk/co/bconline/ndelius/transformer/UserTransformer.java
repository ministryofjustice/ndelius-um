package uk.co.bconline.ndelius.transformer;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.*;
import uk.co.bconline.ndelius.model.entity.*;
import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.service.*;
import uk.co.bconline.ndelius.util.LdapPasswordUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.StringUtils.isEmpty;
import static uk.co.bconline.ndelius.util.NameUtils.*;

@Slf4j
@Component
public class UserTransformer
{
	private static final String OID_DATE_FORMAT = "yyyyMMdd'000000Z'";

	@Value("${ad.primary.principal.suffix:}")
	private String ad1PrincipalSuffix;

	@Value("${ad.secondary.principal.suffix:}")
	private String ad2PrincipalSuffix;

	@Value("${oid.default-password:}")
	private String oidDefaultPassword;

	private final TeamService teamService;
	private final ReferenceDataService referenceDataService;
	private final DatasetService datasetService;
	private final DBUserService dbUserService;
	private final RoleService roleService;
	private final RoleTransformer roleTransformer;
	private final DatasetTransformer datasetTransformer;
	private final ReferenceDataTransformer referenceDataTransformer;

	@Autowired
	public UserTransformer(
			TeamService teamService,
			ReferenceDataService referenceDataService,
			DatasetService datasetService,
			DBUserService dbUserService,
			RoleService roleService,
			RoleTransformer roleTransformer,
			DatasetTransformer datasetTransformer,
			ReferenceDataTransformer referenceDataTransformer)
	{
		this.teamService = teamService;
		this.referenceDataService = referenceDataService;
		this.datasetService = datasetService;
		this.dbUserService = dbUserService;
		this.roleService = roleService;
		this.roleTransformer = roleTransformer;
		this.datasetTransformer = datasetTransformer;
		this.referenceDataTransformer = referenceDataTransformer;
	}

	public SearchResult map(User user)
	{
		return SearchResult.builder()
				.username(user.getUsername())
				.forenames(user.getForenames())
				.surname(user.getSurname())
				.teams(user.getTeams())
				.staffCode(user.getStaffCode())
				.sources(user.getSources())
				.build();
	}

	public Optional<User> map(UserEntity user)
	{
		return combine(user, null, null, null);
	}

	public Optional<User> map(OIDUser user)
	{
		return combine(null, user, null, null);
	}

	public Optional<User> combine(UserEntity dbUser, OIDUser oidUser, ADUser ad1User, ADUser ad2User)
	{
		LocalDateTime t = LocalDateTime.now();
		val allRoles = roleService.getAllRoles().stream().map(OIDRole::getName).collect(toSet());
		log.trace("--{}ms	Get all roles for mapping", MILLIS.between(t, LocalDateTime.now()));
		t = LocalDateTime.now();
		val r = Stream.of(
				ofNullable(oidUser).map(v -> User.builder()
						.username(v.getUsername())
						.forenames(v.getForenames())
						.surname(v.getSurname())
						.email(v.getEmail())
						.privateSector("private".equalsIgnoreCase(v.getSector()))
						.homeArea(datasetService.getDatasetByCode(v.getHomeArea()).orElse(null))
						.endDate(ofNullable(v.getEndDate()).map(s ->
								LocalDate.parse(s.substring(0, 8), ofPattern(OID_DATE_FORMAT.substring(0, 8)))).orElse(null))
						.roles(ofNullable(v.getRoles())
								.map(l -> l.stream().filter(role -> allRoles.contains(role.getName())))
								.map(transactions -> transactions
										.map(roleTransformer::map)
										.peek(role -> {
											// Temporary disabling of add-user
											if (role.getInteractions() != null) role.getInteractions().remove("UMBI003");
										})
										.collect(toList()))
								.orElse(null))
						.sources(singletonList("OID"))
						.build()),
				ofNullable(dbUser).map(v -> User.builder()
						.username(v.getUsername())
						.forenames(combineNames(v.getForename(), v.getForename2()))
						.surname(v.getSurname())
						.datasets(datasetTransformer.map(v.getDatasets()))
						.staffCode(ofNullable(v.getStaff()).map(StaffEntity::getCode).orElse(null))
						.staffGrade(ofNullable(v.getStaff())
								.map(StaffEntity::getGrade)
								.map(referenceDataTransformer::map)
								.orElse(null))
						.startDate(ofNullable(v.getStaff()).map(StaffEntity::getStartDate).orElse(null))
						.endDate(ofNullable(v.getStaff()).map(StaffEntity::getEndDate).filter(Objects::nonNull).orElse(v.getEndDate()))
						.teams(ofNullable(v.getStaff()).map(StaffEntity::getTeams).map(this::map).orElse(null))
						.sources(singletonList("DB"))
						.build()),
				ofNullable(ad1User).map(v -> User.builder()
						.username(v.getUsername())
						.forenames(v.getForename())
						.surname(v.getSurname())
						.sources(singletonList("AD1"))
						.build()),
				ofNullable(ad2User).map(v -> User.builder()
						.username(v.getUsername())
						.forenames(v.getForename())
						.surname(v.getSurname())
						.sources(singletonList("AD2"))
						.build()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.reduce(this::reduceUser);
		log.trace("--{}ms	Combine results", MILLIS.between(t, LocalDateTime.now()));
		return r;
	}

	public Optional<SearchResult> mapToSearchResult(UserEntity dbUser, OIDUser oidUser, ADUser ad1User, ADUser ad2User)
	{
		val t = LocalDateTime.now();
		val r = Stream.of(
				ofNullable(oidUser).map(v -> User.builder()
						.username(v.getUsername())
						.forenames(v.getForenames())
						.surname(v.getSurname())
						.email(v.getEmail())
						.sources(singletonList("OID"))
						.build()),
				ofNullable(dbUser).map(v -> User.builder()
						.username(v.getUsername())
						.forenames(combineNames(v.getForename(), v.getForename2()))
						.surname(v.getSurname())
						.staffCode(ofNullable(v.getStaff()).map(StaffEntity::getCode).orElse(null))
						.teams(ofNullable(v.getStaff()).map(StaffEntity::getTeams).map(this::map).orElse(null))
						.sources(singletonList("DB"))
						.build()),
				ofNullable(ad1User).map(v -> User.builder()
						.username(v.getUsername())
						.forenames(v.getForename())
						.surname(v.getSurname())
						.sources(singletonList("AD1"))
						.build()),
				ofNullable(ad2User).map(v -> User.builder()
						.username(v.getUsername())
						.forenames(v.getForename())
						.surname(v.getSurname())
						.sources(singletonList("AD2"))
						.build()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.reduce(this::reduceUser)
				.map(this::map);
		log.trace("--{}ms	Map result", MILLIS.between(t, LocalDateTime.now()));
		return r;
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
				.teams(ofNullable(a.getTeams()).orElse(b.getTeams()))
				.datasets(ofNullable(a.getDatasets()).orElse(b.getDatasets()))
				.roles(ofNullable(a.getRoles()).orElse(b.getRoles()))
				.email(ofNullable(a.getEmail()).orElse(b.getEmail()))
				.sources(Stream.concat(a.getSources().stream(), b.getSources().stream()).collect(toList()))
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
		val myUserId = dbUserService.getMyUserId();
		val staff = mapToStaffEntity(user, ofNullable(existingUser.getStaff()).orElse(new StaffEntity()));
		val entity = existingUser.toBuilder()
				.username(user.getUsername())
				.forename(staff.getForename())
				.forename2(staff.getForename2())
				.surname(user.getSurname())
				.privateUser(user.getPrivateSector())
				.endDate(user.getEndDate())
				.organisation(ofNullable(user.getHomeArea())
						.map(Dataset::getCode)
						.flatMap(datasetService::getOrganisationIdByDatasetCode)
						.map(OrganisationEntity::new).orElse(null))
				.staff(isEmpty(staff.getCode())? null: staff)
				.build();
		entity.getProbationAreaLinks().clear();
		entity.getProbationAreaLinks().addAll(user.getDatasets().stream()
				.map(dataset -> datasetService.getDatasetId(dataset.getCode()).orElse(null))
				.filter(Objects::nonNull)
				.map(ProbationAreaEntity::new)
				.map(dataset -> ProbationAreaUserEntity.builder()
						.id(new ProbationAreaUserId(dataset, entity))
						.createdById(myUserId).createdAt(LocalDateTime.now())
						.updatedById(myUserId).updatedAt(LocalDateTime.now())
						.build())
				.collect(Collectors.toSet()));
		staff.setUser(singleton(entity));
		return entity;
	}

	public StaffEntity mapToStaffEntity(User user, StaffEntity existingStaff)
	{
		val myUserId = dbUserService.getMyUserId();
		val entity = existingStaff.toBuilder()
				.code(user.getStaffCode())
				.grade(ofNullable(user.getStaffGrade())
						.map(ReferenceData::getCode)
						.flatMap(referenceDataService::getStaffGradeId)
						.map(ReferenceDataEntity::new)
						.orElse(null))
				.forename(firstForename(user.getForenames()))
				.forename2(subsequentForenames(user.getForenames()))
				.surname(user.getSurname())
				.privateStaff(user.getPrivateSector())
				.startDate(user.getStartDate())
				.endDate(user.getEndDate())
				.probationAreaId(ofNullable(user.getHomeArea())
						.map(Dataset::getCode)
						.flatMap(datasetService::getDatasetId)
						.orElse(null))
				.createdAt(ofNullable(existingStaff.getCreatedAt()).orElse(LocalDateTime.now()))
				.createdById(ofNullable(existingStaff.getCreatedById()).orElse(myUserId))
				.updatedAt(LocalDateTime.now())
				.updatedById(myUserId)
				.build();
		entity.getTeamLinks().clear();
		entity.getTeamLinks().addAll(ofNullable(user.getTeams()).map(list -> list.stream()
				.map(team -> teamService.getTeamId(team.getCode()).orElse(null))
				.filter(Objects::nonNull)
				.map(id -> StaffTeamEntity.builder()
						.id(new StaffTeamId(entity, new TeamEntity(id)))
						.createdById(myUserId).createdAt(LocalDateTime.now())
						.updatedById(myUserId).updatedAt(LocalDateTime.now())
						.build())
				.collect(toSet()))
				.orElse(emptySet()));
		return entity;
	}

	public OIDUser mapToOIDUser(User user, OIDUser existingUser)
	{
		return existingUser.toBuilder()
				.username(user.getUsername())
				.uid(user.getUsername())
				.password(LdapPasswordUtils.fixPassword(ofNullable(existingUser.getPassword()).orElse(oidDefaultPassword)))
				.forenames(user.getForenames())
				.surname(user.getSurname())
				.email(user.getEmail())
				.endDate(ofNullable(user.getEndDate()).map(d -> d.format(ofPattern(OID_DATE_FORMAT))).orElse(null))
				.sector(user.getPrivateSector()? "private": "public")
				.homeArea(ofNullable(user.getHomeArea()).map(Dataset::getCode).orElse(null))
				.roles(ofNullable(user.getRoles()).map(list -> list.stream()
						.map(roleTransformer::map)
						.collect(toSet()))
						.orElse(emptySet()))
				.build();
	}

	public ADUser mapToAD2User(User user, ADUser existingUser)
	{
		return existingUser.toBuilder()
				.username(user.getUsername())
				.userPrincipalName(user.getUsername() + ad2PrincipalSuffix)
				.forename(user.getForenames())
				.surname(user.getSurname())
				.displayName(combineNames(user.getForenames(), user.getSurname()))
				.build();
	}

	public ADUser mapToAD1User(User user, ADUser existingUser)
	{
		return existingUser.toBuilder()
				.username(user.getUsername())
				.userPrincipalName(user.getUsername() + ad1PrincipalSuffix)
				.forename(user.getForenames())
				.surname(user.getSurname())
				.displayName(combineNames(user.getForenames(), user.getSurname()))
				.build();
	}
}
