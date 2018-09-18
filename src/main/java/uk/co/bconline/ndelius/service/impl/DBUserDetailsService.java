package uk.co.bconline.ndelius.service.impl;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entity.SearchResultEntity;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.repository.db.ProbationAreaUserRepository;
import uk.co.bconline.ndelius.repository.db.SearchResultRepository;
import uk.co.bconline.ndelius.repository.db.StaffTeamRepository;
import uk.co.bconline.ndelius.repository.db.UserEntityRepository;

@Slf4j
@Service
public class DBUserDetailsService
{
	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	private final UserEntityRepository repository;
	private final SearchResultRepository searchResultRepository;
	private final ProbationAreaUserRepository probationAreaUserRepository;
	private final StaffTeamRepository staffTeamRepository;

	@Autowired
	public DBUserDetailsService(
			UserEntityRepository repository,
			SearchResultRepository searchResultRepository,
			ProbationAreaUserRepository probationAreaUserRepository,
			StaffTeamRepository staffTeamRepository)
	{
		this.repository = repository;
		this.searchResultRepository = searchResultRepository;
		this.probationAreaUserRepository = probationAreaUserRepository;
		this.staffTeamRepository = staffTeamRepository;
	}

	public Optional<UserEntity> getUser(String username)
	{
		val t = LocalDateTime.now();
		val u =  repository.getUserEntityByUsername(username);
		log.trace("--{}ms	DB lookup", MILLIS.between(t, LocalDateTime.now()));
		return u;
	}

	public Long getUserId(String username)
	{
		return getUser(username).map(UserEntity::getId).orElse(null);
	}

	public Long getMyUserId()
	{
		val username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		return getUserId(username);
	}

	@Transactional
	public List<SearchResult> search(String searchTerm)
	{
		val results = Arrays.stream(searchTerm.split("\\s+"))
				.flatMap(token -> {
					log.debug("Searching DB: {}", token);
					if (datasourceUrl.startsWith("jdbc:oracle")) return searchResultRepository.search(token).stream();
					else return searchResultRepository.simpleSearch(token).stream();
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
		log.debug("Found {} DB results", results.size());
		return results;
	}

	@Transactional
	public UserEntity save(UserEntity user)
	{
		val existingUser = getUser(user.getUsername());
		if (existingUser.isPresent())
		{
			probationAreaUserRepository.deleteAll(existingUser.get().getProbationAreaLinks());
			probationAreaUserRepository.saveAll(user.getProbationAreaLinks());
			ofNullable(existingUser.get().getStaff()).map(StaffEntity::getTeamLinks).ifPresent(staffTeamRepository::deleteAll);
			ofNullable(user.getStaff()).map(StaffEntity::getTeamLinks).ifPresent(staffTeamRepository::saveAll);
			return repository.save(user);
		}
		else
		{
			val newUser = repository.saveAndFlush(user);
			probationAreaUserRepository.saveAll(user.getProbationAreaLinks());
			ofNullable(user.getStaff()).map(StaffEntity::getTeamLinks).ifPresent(staffTeamRepository::saveAll);
			return newUser;
		}
	}
}
