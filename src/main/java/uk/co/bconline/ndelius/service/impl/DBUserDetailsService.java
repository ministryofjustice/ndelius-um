package uk.co.bconline.ndelius.service.impl;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hibernate.search.jpa.Search.getFullTextEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.hibernate.search.exception.EmptyQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.repository.db.DatasetRepository;
import uk.co.bconline.ndelius.repository.db.UserEntityRepository;
import uk.co.bconline.ndelius.transformer.DatasetTransformer;

@Slf4j
@Service
public class DBUserDetailsService
{
	private static LocalDateTime lastIndexed;

	private final UserEntityRepository repository;
	private final DatasetRepository datasetRepository;
	private final DatasetTransformer datasetTransformer;
	private final EntityManager entityManager;

	@Value("${spring.jpa.properties.hibernate.search.default.expiry}")
	private int indexExpiry;

	@Autowired
	public DBUserDetailsService(UserEntityRepository repository, DatasetRepository datasetRepository, DatasetTransformer datasetTransformer, EntityManager entityManager)
	{
		this.repository = repository;
		this.datasetRepository = datasetRepository;
		this.datasetTransformer = datasetTransformer;
		this.entityManager = entityManager;
	}

	public Optional<UserEntity> getUser(String username)
	{
		return repository.getUserEntityByUsernameEqualsIgnoreCase(username);
	}

	public List<Dataset> getDatasets(String username)
	{
		return datasetRepository.findAllByUsersWithDatasetUsername(username).stream()
				.map(datasetTransformer::map)
				.collect(toList());
	}

	@Transactional
	public List<UserEntity> search(String searchTerm)
	{
		if (lastIndexed == null || now().minusSeconds(indexExpiry).isAfter(lastIndexed)) reIndex();

		try
		{
			val entityManager = getFullTextEntityManager(this.entityManager);
			val builder = entityManager.getSearchFactory().buildQueryBuilder().forEntity(UserEntity.class).get();
			List<?> results = entityManager.createFullTextQuery(builder
					.keyword()
					.fuzzy()
					.withPrefixLength(1)
					.onFields("username", "forename", "forename2", "surname",
							"staff.code", "staff.team.code", "staff.team.description")
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

	private synchronized void reIndex()
	{
		try
		{
			log.debug("Rebuilding search index");
			val start = now();
			getFullTextEntityManager(entityManager)
					.createIndexer()
					.purgeAllOnStart(true)
					.startAndWait();
			lastIndexed = now();
			log.info("Indexing completed in {}ms", MILLIS.between(start, lastIndexed));
		}
		catch (InterruptedException e)
		{
			log.error("Search index rebuild interrupted", e);
		}
	}
}
