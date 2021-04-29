package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.util.Optionals;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entity.SearchResultEntity;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.repository.db.*;
import uk.co.bconline.ndelius.service.UserEntityService;
import uk.co.bconline.ndelius.transformer.SearchResultTransformer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.singleton;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;
import static uk.co.bconline.ndelius.util.AuthUtils.myUsername;

@Slf4j
@Service
public class UserEntityServiceImpl implements UserEntityService
{
	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	private final UserEntityRepository repository;
	private final StaffRepository staffRepository;
	private final SearchResultRepository searchResultRepository;
	private final ProbationAreaUserRepository probationAreaUserRepository;
	private final StaffTeamRepository staffTeamRepository;
	private final ChangeNoteRepository changeNoteRepository;
	private final SearchResultTransformer searchResultTransformer;

	@Autowired
	public UserEntityServiceImpl(
			UserEntityRepository repository,
			StaffRepository staffRepository,
			SearchResultRepository searchResultRepository,
			ProbationAreaUserRepository probationAreaUserRepository,
			StaffTeamRepository staffTeamRepository,
			ChangeNoteRepository changeNoteRepository,
			SearchResultTransformer searchResultTransformer)
	{
		this.repository = repository;
		this.staffRepository = staffRepository;
		this.searchResultRepository = searchResultRepository;
		this.probationAreaUserRepository = probationAreaUserRepository;
		this.staffTeamRepository = staffTeamRepository;
		this.changeNoteRepository = changeNoteRepository;
		this.searchResultTransformer = searchResultTransformer;
	}

	@Override
	public boolean usernameExists(String username)
	{
		return repository.existsByUsernameIgnoreCase(username);
	}

	@Override
	public Optional<UserEntity> getUser(String username)
	{
		val t = LocalDateTime.now();
		val u =  repository.findFirstByUsernameIgnoreCase(username);
		log.trace("--{}ms	DB lookup", MILLIS.between(t, LocalDateTime.now()));
		return u;
	}

	@Override
	public Optional<StaffEntity> getStaffByStaffCode(String code)
	{
		try
		{
			return staffRepository.findByCodeAndEndDateIsNull(code).or(() -> staffRepository.findByCode(code));
		}
		catch (IncorrectResultSizeDataAccessException e)
		{
			throw new AppException("Unable to select a unique Staff Record for code: " + code);
		}
	}

	@Override
	public Optional<UserEntity> getUserByStaffCode(String code)
	{
		return getStaffByStaffCode(code)
				.map(s -> s.getUser().isEmpty()?
						UserEntity.builder().staff(s).build():
						s.getUser().iterator().next());
	}

	@Override
	public long getUserId(String username) {
		return repository.getUserId(username).orElseThrow(() ->
				new UsernameNotFoundException(String.format("Unable to find Entity ID for user '%s'", username)));
	}

	@Override
	public long getMyUserId() {
		return getUserId(myUsername());
	}

	@Override
	public List<SearchResult> search(String searchTerm, boolean includeInactiveUsers, Set<String> datasets)
	{
		val t = LocalDateTime.now();
		val results = Arrays.stream(searchTerm.trim().split("\\s+"))
				.parallel()
				.flatMap(token -> searchForToken(token, includeInactiveUsers, datasets))
				.collect(groupingBy(SearchResultEntity::getUsername))
				.values()
				.stream()
				.map(list -> list.stream().reduce(searchResultTransformer::reduce))
				.flatMap(Optionals::toStream)
				.map(searchResultTransformer::map)
				.collect(toList());
		log.debug("Found {} DB results in {}ms", results.size(), MILLIS.between(t, LocalDateTime.now()));
		return results;
	}

	private Stream<SearchResultEntity> searchForToken(String token, boolean includeInactiveUsers, Set<String> datasets)
	{
		log.debug("Searching DB: {}", token);
		var filterDatasets = true;
		if (isEmpty(datasets)) {
			filterDatasets = false;
			datasets = singleton("");
		}
		val isOracle = datasourceUrl.startsWith("jdbc:oracle");
		return (isOracle?
					searchResultRepository.search(token, includeInactiveUsers, filterDatasets, datasets):
					searchResultRepository.simpleSearch(token, includeInactiveUsers, filterDatasets, datasets))
				.stream()
				.collect(groupingBy(SearchResultEntity::getUsername))
				.values().stream()
				.map(list -> list.stream().reduce(searchResultTransformer::reduceTeams))
				.flatMap(Optionals::toStream);
	}

	@Override
	public UserEntity save(UserEntity user) {
		val t = LocalDateTime.now();
		// Staff/Teams
		ofNullable(user.getStaff()).ifPresent(staff -> {
			log.debug("Retrieving existing staff");
			ofNullable(staff.getId()).flatMap(staffRepository::findById).ifPresent(existingStaff -> {
				log.debug("Unlinking any other users with the same staff code");
				unlinkOtherUsersFromStaff(user, existingStaff);
				log.debug("Deleting team links");
				staffTeamRepository.deleteAll(existingStaff.getTeamLinks());
			});
			log.debug("Saving staff");
			staffRepository.save(staff);
			log.debug("Saving team links");
			staffTeamRepository.saveAll(staff.getTeamLinks());
		});

		// User/Datasets
		log.debug("Retrieving existing user");
		ofNullable(user.getId()).flatMap(repository::findById).ifPresent(existingUser -> {
			handlePreviousStaffRecord(user.getStaff(), existingUser.getStaff());
			log.debug("Deleting datasets");
			probationAreaUserRepository.deleteAll(existingUser.getProbationAreaLinks());
		});
		log.debug("Saving user");
		val savedUser = repository.save(user);
		log.debug("Saving new datasets");
		probationAreaUserRepository.saveAll(user.getProbationAreaLinks());
		log.debug("Updating user history");
		changeNoteRepository.saveAll(user.getHistory());

		log.debug("Finished saving user to database in {}ms", MILLIS.between(t, LocalDateTime.now()));
		return savedUser;
	}

	private void unlinkOtherUsersFromStaff(UserEntity user, StaffEntity existingStaff) {
		// This is required due to the OneToOne user/staff relationship being modelled in the database as a OneToMany
		val userId = ofNullable(user).map(UserEntity::getId).orElse(null);
		ofNullable(existingStaff).map(StaffEntity::getUser)
				.ifPresent(users -> users.stream()
						.filter(u -> !u.getId().equals(userId))
						.map(u -> u.toBuilder().staff(null).createdBy(null).updatedBy(null).build())
						.forEach(repository::save));
	}

	// When a user is given a new staff code, an End Date should be added to their previous staff record.
	private void handlePreviousStaffRecord(StaffEntity newStaff, StaffEntity previousStaff) {
		if (previousStaff == null || StringUtils.isEmpty(previousStaff.getCode())) {
			// the user never had a staff code - nothing to end-date
			return;
		}
		if (newStaff == null || StringUtils.isEmpty(newStaff.getCode()) ||
				!newStaff.getCode().equals(previousStaff.getCode())) {
			// staff code removed or changed - set an end-date of yesterday to the old one
			log.debug(String.format("Adding an end-date to previous staff record (%s)", previousStaff.getCode()));
			staffRepository.save(previousStaff.toBuilder()
					.endDate(now().minus(1, DAYS))
					.updatedAt(LocalDateTime.now())
					.updatedById(getMyUserId())
					.build());
		}
	}
}
