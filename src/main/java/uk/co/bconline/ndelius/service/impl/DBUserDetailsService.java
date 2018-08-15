package uk.co.bconline.ndelius.service.impl;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.hibernate.search.engine.ProjectionConstants.SCORE;
import static org.hibernate.search.jpa.Search.getFullTextEntityManager;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.hibernate.search.exception.EmptyQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.repository.db.ProbationAreaUserRepository;
import uk.co.bconline.ndelius.repository.db.StaffTeamRepository;
import uk.co.bconline.ndelius.repository.db.UserEntityRepository;
import uk.co.bconline.ndelius.util.SearchIndexHelper;

@Slf4j
@Service
public class DBUserDetailsService
{
	private final UserEntityRepository repository;
	private final ProbationAreaUserRepository probationAreaUserRepository;
	private final StaffTeamRepository staffTeamRepository;
	private final EntityManager entityManager;
	private final SearchIndexHelper searchIndexHelper;

	@Autowired
	public DBUserDetailsService(
			UserEntityRepository repository,
			ProbationAreaUserRepository probationAreaUserRepository,
			StaffTeamRepository staffTeamRepository,
			EntityManager entityManager,
			SearchIndexHelper searchIndexHelper)
	{
		this.repository = repository;
		this.probationAreaUserRepository = probationAreaUserRepository;
		this.staffTeamRepository = staffTeamRepository;
		this.entityManager = entityManager;
		this.searchIndexHelper = searchIndexHelper;
	}

	public Optional<UserEntity> getUser(String username)
	{
		return repository.getUserEntityByUsernameEqualsIgnoreCase(username);
	}

	public Optional<UserEntity> getUserByStaffCode(String staffCode)
	{
		return repository.getUserEntityByStaffCodeEqualsIgnoreCase(staffCode);
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
		if (searchIndexHelper.indexExpired()) searchIndexHelper.reIndex();

		try
		{
			val fullText = getFullTextEntityManager(this.entityManager);
			val builder = fullText.getSearchFactory().buildQueryBuilder().forEntity(UserEntity.class).get();
			List<?> results = fullText.createFullTextQuery(builder
					.keyword()
					.fuzzy()
					.onFields("username", "forename", "forename2", "surname",
							"staff.code", "staff.teamLinks.team.code", "staff.teamLinks.team.description")
					.matching(searchTerm)
					.createQuery(), UserEntity.class)
					.setProjection("username", SCORE)
					.getResultList();

			return results.stream()
					.map(Object[].class::cast)
					.map(res -> SearchResult.builder()
							.username((String) res[0])
							.score((float) res[1])
							.build())
					.collect(toList());
		}
		catch (EmptyQueryException e)
		{
			log.debug("Analyzed query was empty: '{}'", searchTerm);
			return emptyList();
		}
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
			getFullTextEntityManager(entityManager).flushToIndexes();
			probationAreaUserRepository.saveAll(user.getProbationAreaLinks());
			ofNullable(user.getStaff()).map(StaffEntity::getTeamLinks).ifPresent(staffTeamRepository::saveAll);
			return newUser;
		}
	}
}
