package uk.co.bconline.ndelius.transformer;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.ReferenceData;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.*;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.service.*;
import uk.co.bconline.ndelius.util.LdapUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.StringUtils.isEmpty;
import static uk.co.bconline.ndelius.util.LdapUtils.mapOIDStringToDate;
import static uk.co.bconline.ndelius.util.LdapUtils.mapToOIDString;
import static uk.co.bconline.ndelius.util.NameUtils.*;

@Slf4j
@Component
public class UserTransformer
{
	@Value("${oid.default-password:#{null}}")
	private String defaultPassword;

	private final TeamService teamService;
	private final ReferenceDataService referenceDataService;
	private final DatasetService datasetService;
	private final DBUserService dbUserService;
	private final RoleService roleService;
	private final RoleTransformer roleTransformer;
	private final DatasetTransformer datasetTransformer;
	private final ReferenceDataTransformer referenceDataTransformer;
	private final TeamTransformer teamTransformer;

	@Autowired
	public UserTransformer(
			TeamService teamService,
			ReferenceDataService referenceDataService,
			DatasetService datasetService,
			DBUserService dbUserService,
			RoleService roleService,
			RoleTransformer roleTransformer,
			DatasetTransformer datasetTransformer,
			ReferenceDataTransformer referenceDataTransformer,
			TeamTransformer teamTransformer)
	{
		this.teamService = teamService;
		this.referenceDataService = referenceDataService;
		this.datasetService = datasetService;
		this.dbUserService = dbUserService;
		this.roleService = roleService;
		this.roleTransformer = roleTransformer;
		this.datasetTransformer = datasetTransformer;
		this.referenceDataTransformer = referenceDataTransformer;
		this.teamTransformer = teamTransformer;
	}

	public Optional<User> map(UserEntity user)
	{
		return ofNullable(user).map(v -> User.builder()
				.username(v.getUsername())
				.forenames(combineNames(v.getForename(), v.getForename2()))
				.surname(v.getSurname())
				.datasets(datasetTransformer.mapToDatasets(v.getDatasets()))
				.establishments(datasetTransformer.mapToEstablishments(v.getDatasets()))
				.staffCode(ofNullable(v.getStaff()).map(StaffEntity::getCode).orElse(null))
				.staffGrade(ofNullable(v.getStaff())
						.map(StaffEntity::getGrade)
						.map(referenceDataTransformer::map)
						.orElse(null))
				.subContractedProvider(ofNullable(v.getStaff())
						.map(StaffEntity::getSubContractedProvider)
						.map(datasetTransformer::map)
						.orElse(null))
				.startDate(ofNullable(v.getStaff()).map(StaffEntity::getStartDate).orElse(null))
				.endDate(ofNullable(v.getStaff()).map(StaffEntity::getEndDate).filter(Objects::nonNull).orElseGet(v::getEndDate))
				.teams(ofNullable(v.getStaff()).map(StaffEntity::getTeams).map(teamTransformer::map).orElse(null))
				.createdAt(v.getCreatedAt())
				.createdBy(ofNullable(v.getCreatedBy())
						.map(c -> combineNames(c.getForename(), c.getForename2(), c.getSurname()))
						.orElse(null))
				.updatedAt(v.getUpdatedAt())
				.updatedBy(ofNullable(v.getUpdatedBy())
						.map(u -> combineNames(u.getForename(), u.getForename2(), u.getSurname()))
						.orElse(null))
				.sources(singletonList("DB"))
				.build());
	}

	public Optional<User> map(OIDUser user)
	{
		LocalDateTime t = now();
		val allRoles = roleService.getAllRoles().stream().map(OIDRole::getName).collect(toSet());
		log.trace("--{}ms	Get all roles for mapping", MILLIS.between(t, now()));
		return ofNullable(user).map(v -> User.builder()
				.username(v.getUsername())
				.forenames(v.getForenames())
				.surname(v.getSurname())
				.email(v.getEmail())
				.privateSector("private".equalsIgnoreCase(v.getSector()))
				.homeArea(datasetService.getDatasetByCode(v.getHomeArea()).orElse(null))
				.startDate(mapOIDStringToDate(v.getStartDate()))
				.endDate(mapOIDStringToDate(v.getEndDate()))
				.roles(ofNullable(v.getRoles())
						.map(l -> l.stream().filter(role -> allRoles.contains(role.getName())))
						.map(transactions -> transactions
								.map(roleTransformer::map)
								.collect(toList()))
						.orElse(null))
				.sources(singletonList("OID"))
				.build());
	}

	public Optional<User> combine(UserEntity dbUser, OIDUser oidUser)
	{
		val t = now();
		val r = Stream.of(
				map(oidUser),
				map(dbUser))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.reduce(this::reduceUser);
		log.trace("--{}ms	Combine results", MILLIS.between(t, now()));
		return r;
	}

	private User reduceUser(User a, User b)
	{
		return a.toBuilder()
				.username(ofNullable(a.getUsername()).orElseGet(b::getUsername))
				.forenames(ofNullable(a.getForenames()).orElseGet(b::getForenames))
				.surname(ofNullable(a.getSurname()).orElseGet(b::getSurname))
				.staffCode(ofNullable(a.getStaffCode()).orElseGet(b::getStaffCode))
				.staffGrade(ofNullable(a.getStaffGrade()).orElseGet(b::getStaffGrade))
				.homeArea(ofNullable(a.getHomeArea()).orElseGet(b::getHomeArea))
				.startDate(ofNullable(a.getStartDate()).orElseGet(b::getStartDate))
				.endDate(ofNullable(a.getEndDate()).orElseGet(b::getEndDate))
				.teams(ofNullable(a.getTeams()).orElseGet(b::getTeams))
				.datasets(ofNullable(a.getDatasets()).orElseGet(b::getDatasets))
				.establishments(ofNullable(a.getEstablishments()).orElseGet(b::getEstablishments))
				.subContractedProvider(ofNullable(a.getSubContractedProvider()).orElseGet(b::getSubContractedProvider))
				.roles(ofNullable(a.getRoles()).orElseGet(b::getRoles))
				.email(ofNullable(a.getEmail()).orElseGet(b::getEmail))
				.createdAt(ofNullable(a.getCreatedAt()).orElseGet(b::getCreatedAt))
				.createdBy(ofNullable(a.getCreatedBy()).orElseGet(b::getCreatedBy))
				.updatedAt(ofNullable(a.getUpdatedAt()).orElseGet(b::getUpdatedAt))
				.updatedBy(ofNullable(a.getUpdatedBy()).orElseGet(b::getUpdatedBy))
				.sources(Stream.concat(a.getSources().stream(), b.getSources().stream()).collect(toList()))
				.build();
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
				.createdBy(null).updatedBy(null)
				.createdById(ofNullable(existingUser.getCreatedById()).orElse(myUserId))
				.createdAt(ofNullable(existingUser.getCreatedAt()).orElse(now()))
				.updatedById(myUserId)
				.updatedAt(now())
				.build();
		entity.getProbationAreaLinks().clear();
		entity.getProbationAreaLinks().addAll(Stream
				.concat(user.getDatasets().stream(),
						ofNullable(user.getEstablishments()).map(List::stream).orElseGet(Stream::empty))
				.parallel()
				.map(dataset -> datasetService.getDatasetId(dataset.getCode()).orElse(null))
				.filter(Objects::nonNull)
				.map(ProbationAreaEntity::new)
				.map(dataset -> ProbationAreaUserEntity.builder()
						.id(new ProbationAreaUserId(dataset, entity))
						.createdById(myUserId).createdAt(now())
						.updatedById(myUserId).updatedAt(now())
						.build())
				.collect(Collectors.toSet()));
		staff.setUser(singleton(entity));
		return entity;
	}

	public StaffEntity mapToStaffEntity(User user, StaffEntity existingStaff)
	{
		val myUserId = dbUserService.getMyUserId();
		if (user.getStaffCode() != null && !user.getStaffCode().equals(existingStaff.getCode()))
		{
			// staff code has changed, fetch the new staff record to reassign it to this user
			existingStaff = dbUserService.getUserByStaffCode(user.getStaffCode())
					.map(UserEntity::getStaff).orElse(new StaffEntity());
		}
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
				.subContractedProvider(ofNullable(user.getSubContractedProvider())
						.map(Dataset::getCode)
						.flatMap(datasetService::getSubContractedProviderId)
						.map(SubContractedProviderEntity::new)
						.orElse(null))
				.createdAt(ofNullable(existingStaff.getCreatedAt()).orElse(now()))
				.createdById(ofNullable(existingStaff.getCreatedById()).orElse(myUserId))
				.updatedAt(now())
				.updatedById(myUserId)
				.build();
		entity.getTeamLinks().clear();
		entity.getTeamLinks().addAll(ofNullable(user.getTeams()).map(list -> list.parallelStream()
				.map(team -> teamService.getTeamId(team.getCode()).orElse(null))
				.filter(Objects::nonNull)
				.map(id -> StaffTeamEntity.builder()
						.id(new StaffTeamId(entity, new TeamEntity(id)))
						.createdById(myUserId).createdAt(now())
						.updatedById(myUserId).updatedAt(now())
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
				.password(ofNullable(existingUser.getPassword())
						.map(LdapUtils::fixPassword)
						.orElse(ofNullable(defaultPassword).orElseGet(LdapUtils::randomPassword)))
				.forenames(user.getForenames())
				.surname(user.getSurname())
				.email(user.getEmail())
				.startDate(mapToOIDString(user.getStartDate()))
				.endDate(mapToOIDString(user.getEndDate()))
				.sector(user.getPrivateSector()? "private": "public")
				.homeArea(ofNullable(user.getHomeArea()).map(Dataset::getCode).orElse(null))
				.roles(ofNullable(user.getRoles()).map(list -> list.stream()
						.map(roleTransformer::map)
						.collect(toSet()))
						.orElseGet(Collections::emptySet))
				.build();
	}
}
