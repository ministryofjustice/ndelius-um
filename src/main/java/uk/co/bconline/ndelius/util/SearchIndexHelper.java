package uk.co.bconline.ndelius.util;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hibernate.search.jpa.Search.getFullTextEntityManager;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.exception.AppException;

@Slf4j
@Component
public class SearchIndexHelper
{
	private static LocalDateTime lastIndexed;

	@Value("${spring.jpa.properties.hibernate.search.default.expiry}")
	private int indexExpiry;

	private final EntityManager entityManager;

	@Autowired
	public SearchIndexHelper(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	public boolean indexExpired()
	{
		return lastIndexed == null || now().minusSeconds(indexExpiry).isAfter(lastIndexed);
	}

	public void reIndex()
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
			throw new AppException("Search index rebuild interrupted", e);
		}
	}
}