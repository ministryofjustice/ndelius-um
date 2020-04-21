package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Optionals;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entity.SearchResultEntity;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.repository.db.*;
import uk.co.bconline.ndelius.service.UserEntityService;
import uk.co.bconline.ndelius.transformer.SearchResultTransformer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.singleton;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;
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
	private final SearchResultTransformer searchResultTransformer;

	@Autowired
	public UserEntityServiceImpl(
			UserEntityRepository repository,
			StaffRepository staffRepository,
			SearchResultRepository searchResultRepository,
			ProbationAreaUserRepository probationAreaUserRepository,
			StaffTeamRepository staffTeamRepository,
			SearchResultTransformer searchResultTransformer)
	{
		this.repository = repository;
		this.staffRepository = staffRepository;
		this.searchResultRepository = searchResultRepository;
		this.probationAreaUserRepository = probationAreaUserRepository;
		this.staffTeamRepository = staffTeamRepository;
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
	public Optional<UserEntity> getUserByStaffCode(String code)
	{
		val staff = staffRepository.findByCode(code);
		return staff.map(s -> s.getUser().isEmpty()?
				UserEntity.builder().staff(s).build():
				s.getUser().iterator().next());
	}

	@Override
	public long getMyUserId()
	{
		val username = myUsername();
		return repository.getUserId(username).orElseThrow(() ->
				new UsernameNotFoundException(String.format("Unable to find Entity ID for user '%s'", username)));
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
	public UserEntity save(UserEntity user)
	{
		log.debug("Checking for existing user");
		val existingUser = getUser(user.getUsername());
		if (existingUser.isPresent())
		{
			log.debug("Deleting datasets");
			probationAreaUserRepository.deleteAll(existingUser.get().getProbationAreaLinks());
			log.debug("Saving new datasets");
			probationAreaUserRepository.saveAll(user.getProbationAreaLinks());
			log.debug("Saving user/staff");
			val newUser = repository.save(user);
			updateUserTeams(user);
			log.debug("Unlinking any other users with the same staff code");
			// Required due to modelling the OneToOne user/staff relationship as a OneToMany
			ofNullable(user.getStaff()).map(StaffEntity::getCode)
					.flatMap(staffRepository::findByCode).map(StaffEntity::getUser)
					.ifPresent(users -> users.stream()
							.filter(u -> !u.getUsername().equals(user.getUsername()))
							.map(u -> u.toBuilder().staff(null).createdBy(null).updatedBy(null).build())
							.forEach(repository::save));
			log.debug("Finished saving user to database");
			return newUser;
		}
		else
		{
			log.debug("Saving user/staff");
			val newUser = repository.saveAndFlush(user);
			log.debug("Saving new datasets");
			probationAreaUserRepository.saveAll(user.getProbationAreaLinks());
			log.debug("Saving team links");
			ofNullable(user.getStaff()).map(StaffEntity::getTeamLinks).ifPresent(staffTeamRepository::saveAll);
			log.debug("Finished saving new user to database");
			return newUser;
		}
	}

	private void updateUserTeams(UserEntity user)
	{
		log.debug("Deleting any existing team links");
		ofNullable(user.getStaff()).map(StaffEntity::getCode)
				.flatMap(staffRepository::findByCode).map(StaffEntity::getTeamLinks)
				.map(links -> links.stream().filter(Objects::nonNull).collect(toSet()))
				.ifPresent(staffTeamRepository::deleteAll);
		log.debug("Saving team links");
		ofNullable(user.getStaff()).map(StaffEntity::getCode)
				.flatMap(staffRepository::findByCode)
				.flatMap(existingStaff -> ofNullable(user.getStaff())
						.map(StaffEntity::getTeamLinks)
						.map(links -> links.stream()
								.peek(link -> link.getId().setStaff(existingStaff))	// Fix staff id
								.collect(toSet())))
				.ifPresent(staffTeamRepository::saveAll);
	}
}
