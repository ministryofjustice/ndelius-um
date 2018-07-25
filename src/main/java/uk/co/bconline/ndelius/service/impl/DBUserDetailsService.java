package uk.co.bconline.ndelius.service.impl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hibernate.search.jpa.Search.getFullTextEntityManager;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.hibernate.search.exception.EmptyQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.repository.db.UserEntityRepository;
import uk.co.bconline.ndelius.util.SearchIndexHelper;

@Slf4j
@Service
public class DBUserDetailsService
{
	private final UserEntityRepository repository;
	private final EntityManager entityManager;
	private final SearchIndexHelper searchIndexHelper;

	@Autowired
	public DBUserDetailsService(UserEntityRepository repository, EntityManager entityManager, SearchIndexHelper searchIndexHelper)
	{
		this.repository = repository;
		this.entityManager = entityManager;
		this.searchIndexHelper = searchIndexHelper;
	}

	public Optional<UserEntity> getUser(String username)
	{
		return repository.getUserEntityByUsernameEqualsIgnoreCase(username);
	}

	@Transactional
	public List<UserEntity> search(String searchTerm)
	{
		if (searchIndexHelper.indexExpired()) searchIndexHelper.reIndex();

		try
		{
			val fullText = getFullTextEntityManager(this.entityManager);
			val builder = fullText.getSearchFactory().buildQueryBuilder().forEntity(UserEntity.class).get();
			List<?> results = fullText.createFullTextQuery(builder
					.keyword()
					.fuzzy()
					.withPrefixLength(1)
					.onFields("username", "forename", "forename2", "surname",
							"staff.code", "staff.teams.code", "staff.teams.description")
					.matching(searchTerm)
					.createQuery(), UserEntity.class)
					.getResultList();

			return results.stream()
					.map(UserEntity.class::cast)
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
		return repository.save(user);
	}
}
