package uk.co.bconline.ndelius.transformer;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Optionals;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.ExportResult;
import uk.co.bconline.ndelius.model.ReferenceData;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.ChangeNoteEntity;
import uk.co.bconline.ndelius.model.entity.OrganisationEntity;
import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;
import uk.co.bconline.ndelius.model.entity.ProbationAreaUserEntity;
import uk.co.bconline.ndelius.model.entity.ProbationAreaUserId;
import uk.co.bconline.ndelius.model.entity.ReferenceDataEntity;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.StaffTeamEntity;
import uk.co.bconline.ndelius.model.entity.StaffTeamId;
import uk.co.bconline.ndelius.model.entity.SubContractedProviderEntity;
import uk.co.bconline.ndelius.model.entity.TeamEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.entity.export.BoroughExportEntity;
import uk.co.bconline.ndelius.model.entity.export.ProbationAreaExportEntity;
import uk.co.bconline.ndelius.model.entity.export.ReferenceDataExportEntity;
import uk.co.bconline.ndelius.model.entity.export.StaffExportEntity;
import uk.co.bconline.ndelius.model.entity.export.TeamExportEntity;
import uk.co.bconline.ndelius.model.entity.export.UserExportEntity;
import uk.co.bconline.ndelius.model.entry.ClientEntry;
import uk.co.bconline.ndelius.model.entry.RoleEntry;
import uk.co.bconline.ndelius.model.entry.UserEntry;
import uk.co.bconline.ndelius.service.DatasetService;
import uk.co.bconline.ndelius.service.ReferenceDataService;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.service.TeamService;
import uk.co.bconline.ndelius.service.UserEntityService;
import uk.co.bconline.ndelius.service.UserHistoryService;
import uk.co.bconline.ndelius.service.UserRoleService;
import uk.co.bconline.ndelius.util.LdapUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.StringUtils.isEmpty;
import static uk.co.bconline.ndelius.util.LdapUtils.mapLdapStringToDate;
import static uk.co.bconline.ndelius.util.LdapUtils.mapToLdapString;
import static uk.co.bconline.ndelius.util.NameUtils.combineNames;
import static uk.co.bconline.ndelius.util.NameUtils.firstForename;
import static uk.co.bconline.ndelius.util.NameUtils.subsequentForenames;

@Slf4j
@Component
public class UserTransformer
{
	@Value("${spring.ldap.default-password:#{null}}")
	private String defaultPassword;

	private final TeamService teamService;
	private final ReferenceDataService referenceDataService;
	private final DatasetService datasetService;
	private final UserEntityService userEntityService;
	private final RoleService roleService;
	private final UserHistoryService userHistoryService;
	private final RoleTransformer roleTransformer;
	private final DatasetTransformer datasetTransformer;
	private final ReferenceDataTransformer referenceDataTransformer;
	private final TeamTransformer teamTransformer;
	private final GroupTransformer groupTransformer;
	private final ChangeNoteTransformer changeNoteTransformer;
	private final UserRoleService userRoleService;

	@Autowired
	public UserTransformer(
			TeamService teamService,
			ReferenceDataService referenceDataService,
			DatasetService datasetService,
			UserEntityService userEntityService,
			RoleService roleService,
			UserHistoryService userHistoryService,
			RoleTransformer roleTransformer,
			DatasetTransformer datasetTransformer,
			ReferenceDataTransformer referenceDataTransformer,
			TeamTransformer teamTransformer,
			GroupTransformer groupTransformer,
			ChangeNoteTransformer changeNoteTransformer,
			UserRoleService userRoleService)
	{
		this.teamService = teamService;
		this.referenceDataService = referenceDataService;
		this.datasetService = datasetService;
		this.userEntityService = userEntityService;
		this.roleService = roleService;
		this.userHistoryService = userHistoryService;
		this.roleTransformer = roleTransformer;
		this.datasetTransformer = datasetTransformer;
		this.referenceDataTransformer = referenceDataTransformer;
		this.teamTransformer = teamTransformer;
		this.groupTransformer = groupTransformer;
		this.changeNoteTransformer = changeNoteTransformer;
		this.userRoleService = userRoleService;
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
				.created(changeNoteTransformer.map(v.getCreatedBy(), v.getCreatedAt(), null).orElse(null))
				.updated(changeNoteTransformer.map(v.getUpdatedBy(), v.getUpdatedAt(), null).orElse(null))
				.sources(singletonList("DB"))
				.build());
	}

	public Optional<User> map(ClientEntry client)
	{
		LocalDateTime t = now();
		val allRoles = roleService.getAllRoles();
		log.trace("--{}ms	Get all roles for filtering", MILLIS.between(t, now()));
		return ofNullable(client).map(v -> User.builder()
				.username(v.getClientId())
				.roles(roleTransformer.filterAndMap(v.getRoles(), allRoles))
				.sources(singletonList("LDAP"))
				.build());
	}

	public Optional<User> map(UserEntry user)
	{
		LocalDateTime t = now();
		val allRoles = roleService.getAllRoles();
		log.trace("--{}ms	Get all roles for filtering", MILLIS.between(t, now()));
		return ofNullable(user).map(v -> User.builder()
				.username(v.getUsername())
				.forenames(v.getForenames())
				.surname(v.getSurname())
				.email(v.getEmail())
				.telephoneNumber(v.getTelephoneNumber())
				.privateSector("private".equalsIgnoreCase(v.getSector()))
				.homeArea(datasetService.getDatasetByCode(v.getHomeArea()).orElse(null))
				.startDate(mapLdapStringToDate(v.getStartDate()))
				.endDate(mapLdapStringToDate(v.getEndDate()))
				.roles(roleTransformer.filterAndMap(v.getRoles(), allRoles))
				.groups(groupTransformer.groupedByType(v.getGroups()))
				.sources(singletonList("LDAP"))
				.build());
	}

	public ExportResult combine(UserExportEntity db, UserEntry ldap) {
		if (ldap == null) return null;
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		val userRoles = ofNullable(ldap.getUsername()).map(userRoleService::getUserRoles).orElse(emptySet());
		return ExportResult.builder()
				.username(ofNullable(ldap.getUsername()).orElseGet(db::getUsername))
				.forenames(ofNullable(ldap.getForenames()).orElseGet(() -> combineNames(db.getForename(), db.getForename2())))
				.surname(ofNullable(ldap.getSurname()).orElseGet(db::getSurname))
				.email(ldap.getEmail())
				.telephoneNumber(ldap.getTelephoneNumber())
				.homeArea(ldap.getHomeArea())
				.endDate(ofNullable(ldap.getEndDate()).map(LdapUtils::mapLdapStringToDate).orElseGet(db::getEndDate))
				.startDate(ofNullable(ldap.getStartDate()).map(LdapUtils::mapLdapStringToDate).orElseGet(() -> ofNullable(db.getStaff()).map(StaffExportEntity::getStartDate).orElse(null)))
				.sector(ofNullable(ofNullable(ldap.getSector()).map("private"::equalsIgnoreCase).orElseGet(db::getPrivateUser)).map(p -> p ? "Private" : "Public").orElse(null))
				.staffCode(ofNullable(db.getStaff()).map(StaffExportEntity::getCode).orElse(null))
				.staffGrade(ofNullable(db.getStaff()).map(StaffExportEntity::getGrade).map(ReferenceDataExportEntity::getCode).orElse(null))
				.teams(ofNullable(db.getStaff()).map(StaffExportEntity::getTeams).map(l -> l.stream()
						.map(TeamExportEntity::getExportDescription).distinct().sorted().collect(joining(","))).orElse(null))
				.datasets(ofNullable(db.getDatasets()).map(l -> l.stream()
						.map(ProbationAreaExportEntity::getCode).distinct().sorted().collect(joining(","))).orElse(null))
				.lastAccessedDate(db.getLastAccessedDate() != null ? db.getLastAccessedDate().format(format) : null)
				.lau(ofNullable(db.getStaff()).map(StaffExportEntity::getTeams).map(l -> l.stream()
						.map(TeamExportEntity::getLDUDescription).filter(Objects::nonNull).distinct().sorted().collect(joining(","))).orElse(null))
				.pdu(ofNullable(db.getStaff()).map(StaffExportEntity::getTeams).map(l -> l.stream()
						.map(TeamExportEntity::getBoroughDescription).filter(Objects::nonNull).distinct().sorted().collect(joining(","))).orElse(null))
				.provider(ofNullable(db.getStaff()).map(StaffExportEntity::getTeams).map(l -> l.stream()
						.map(TeamExportEntity::getBorough).filter(Objects::nonNull).map(BoroughExportEntity::getProbationArea).map(ProbationAreaExportEntity::getExportDescription).distinct().sorted().collect(joining(","))).orElse(null))
				.roleDescriptions(ofNullable(userRoles).map(l -> l.stream()
						.map(RoleEntry::getName).distinct().sorted().collect(joining(","))).orElse(null))
				.build();
	}

	public Optional<User> combine(UserEntity dbUser, UserEntry userEntry)
	{
		val t = now();
		val r = Stream.of(
				map(userEntry),
				map(dbUser))
				.flatMap(Optionals::toStream)
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
				.telephoneNumber(ofNullable(a.getTelephoneNumber()).orElseGet(b::getTelephoneNumber))
				.created(ofNullable(a.getCreated()).orElseGet(b::getCreated))
				.updated(ofNullable(a.getUpdated()).orElseGet(b::getUpdated))
				.sources(Stream.concat(a.getSources().stream(), b.getSources().stream()).collect(toList()))
				.build();
	}

	public UserEntity mapToUserEntity(User user, UserEntity existingUser) {
		return mapToUserEntity(user, existingUser, null);
	}

	public UserEntity mapToUserEntity(User user, UserEntity existingUser, String existingHomeArea) {
		val updateTime = now();
		val myUserId = userEntityService.getMyUserId();
		val staff = mapToStaffEntity(user, ofNullable(existingUser.getStaff()).orElse(new StaffEntity()), existingHomeArea);
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
				.createdAt(ofNullable(existingUser.getCreatedAt()).orElse(updateTime))
				.updatedById(myUserId)
				.updatedAt(updateTime)
				.build();
		val newHistory = ImmutableSet.<ChangeNoteEntity>builder();
		if (!userHistoryService.hasHistory(existingUser.getId())) {
			// If a user has no history records but has created/updated details, then copy the created/updated details into the history
			changeNoteTransformer.mapToEntity(entity, existingUser.getCreatedBy(), existingUser.getCreatedAt()).ifPresent(newHistory::add);
			changeNoteTransformer.mapToEntity(entity, existingUser.getUpdatedBy(), existingUser.getUpdatedAt()).ifPresent(newHistory::add);
		}
		entity.setHistory(newHistory.add(ChangeNoteEntity.builder()
				.user(entity)
				.updatedById(myUserId)
				.updatedAt(updateTime)
				.notes(user.getChangeNote())
				.build()).build());
		entity.getProbationAreaLinks().clear();
		entity.getProbationAreaLinks().addAll(Stream
				.concat(user.getDatasets().stream(),
						ofNullable(user.getEstablishments()).map(List::stream).orElseGet(Stream::empty))
				.map(dataset -> datasetService.getDatasetId(dataset.getCode()).orElse(null))
				.filter(Objects::nonNull)
				.map(ProbationAreaEntity::new)
				.map(dataset -> ProbationAreaUserEntity.builder()
						.id(new ProbationAreaUserId(dataset, entity))
						.createdById(myUserId).createdAt(updateTime)
						.updatedById(myUserId).updatedAt(updateTime)
						.build())
				.collect(Collectors.toSet()));
		staff.setUser(singleton(entity));
		return entity;
	}

	public StaffEntity mapToStaffEntity(User user, StaffEntity existingStaff, String existingHomeArea) {
		val myUserId = userEntityService.getMyUserId();
		if (user.getStaffCode() != null && !user.getStaffCode().equals(existingStaff.getCode())) {
			// staff code has changed, fetch the new staff record to reassign it to this user
			existingStaff = userEntityService.getStaffByStaffCode(user.getStaffCode()).orElse(new StaffEntity());
		} else {
			// staff code has not changed, if home area has changed then the staff record should be unlinked
			if (user.getHomeArea() != null && !user.getHomeArea().getCode().equals(existingHomeArea)) {
				user.setStaffCode(null);
			}
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
		entity.getTeamLinks().addAll(ofNullable(user.getTeams()).map(list -> list.stream()
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

	public UserEntry mapToUserEntry(User user, UserEntry existingUser)
	{
		return existingUser.toBuilder()
				.username(user.getUsername())
				.uid(user.getUsername())
				.password(ofNullable(existingUser.getPassword())
						.map(LdapUtils::fixPassword)
						.orElseGet(() -> ofNullable(defaultPassword).orElseGet(LdapUtils::randomPassword)))
				.forenames(user.getForenames())
				.surname(user.getSurname())
				.email(user.getEmail())
				.telephoneNumber(isEmpty(user.getTelephoneNumber())? null: user.getTelephoneNumber())
				.startDate(mapToLdapString(user.getStartDate()))
				.endDate(mapToLdapString(user.getEndDate()))
				.sector(user.getPrivateSector()? "private": "public")
				.homeArea(ofNullable(user.getHomeArea()).map(Dataset::getCode).orElse(null))
				.roles(ofNullable(user.getRoles()).map(list -> list.stream()
						.map(roleTransformer::map)
						.collect(toSet()))
						.orElseGet(Collections::emptySet))
				.groupNames(groupTransformer.mapToNames(groupTransformer.collate(user.getGroups())))
				.build();
	}
}
