package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entity.SearchResultEntity;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.repository.db.*;
import uk.co.bconline.ndelius.service.DBUserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;

@Slf4j
@Service
public class DBUserServiceImpl implements DBUserService
{
	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	private final UserEntityRepository repository;
	private final StaffRepository staffRepository;
	private final SearchResultRepository searchResultRepository;
	private final ProbationAreaUserRepository probationAreaUserRepository;
	private final StaffTeamRepository staffTeamRepository;

	@Autowired
	public DBUserServiceImpl(
			UserEntityRepository repository,
			StaffRepository staffRepository,
			SearchResultRepository searchResultRepository,
			ProbationAreaUserRepository probationAreaUserRepository,
			StaffTeamRepository staffTeamRepository)
	{
		this.repository = repository;
		this.staffRepository = staffRepository;
		this.searchResultRepository = searchResultRepository;
		this.probationAreaUserRepository = probationAreaUserRepository;
		this.staffTeamRepository = staffTeamRepository;
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

	private Long getUserId(String username)
	{
		return repository.getUserId(username).orElse(null);
	}

	@Override
	public Long getMyUserId()
	{
		val username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		return getUserId(username);
	}

	@Override
	public List<SearchResult> search(String searchTerm, boolean includeInactiveUsers)
	{
		val t = LocalDateTime.now();
		val results = Arrays.stream(searchTerm.trim().split("\\s+"))
				.parallel()
				.flatMap(token -> {
					log.debug("Searching DB: {}", token);
					if (datasourceUrl.startsWith("jdbc:oracle")) return searchResultRepository.search(token, includeInactiveUsers).stream();
					else return searchResultRepository.simpleSearch(token, includeInactiveUsers).stream();
				})
				.collect(groupingBy(SearchResultEntity::getUsername))
				.values()
				.stream()
				.map(list -> list.stream().reduce((a, b) -> SearchResultEntity.builder()
						.username(a.getUsername())
						.score(a.getScore() + b.getScore())
						.build()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(entity -> SearchResult.builder()
						.username(entity.getUsername())
						.score(entity.getScore())
						.build())
				.collect(toList());
		log.debug("Found {} DB results in {}ms", results.size(), MILLIS.between(t, LocalDateTime.now()));
		return results;
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
