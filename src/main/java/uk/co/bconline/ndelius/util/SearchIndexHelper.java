package uk.co.bconline.ndelius.util;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.exception.AppException;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hibernate.search.jpa.Search.getFullTextEntityManager;

@Slf4j
@Component
public class SearchIndexHelper
{
	@Setter
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