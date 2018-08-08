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

@Slf4j
@Component
public class SearchIndexHelper
{
	@Value("${spring.jpa.properties.hibernate.search.default.expiry}")
	private int indexExpiry;

	private LocalDateTime lastIndexed;
	private final EntityManager entityManager;

	@Autowired
	public SearchIndexHelper(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	public synchronized boolean indexExpired()
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
			setLastIndexed(now());
			log.info("Indexing completed in {}ms", MILLIS.between(start, lastIndexed));
		}
		catch (InterruptedException e)
		{
			log.error("Search index rebuild interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	private synchronized void setLastIndexed(LocalDateTime lastIndexed)
	{
		this.lastIndexed = lastIndexed;
	}
}