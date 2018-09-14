package uk.co.bconline.ndelius.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig
{
	private static final int EVICT_DELAY_MINUTES = 6 * 60;	// 6 hours

	@Bean
	public CacheManager cacheManager()
	{
		return new ConcurrentMapCacheManager("roles");
	}

	@CacheEvict(value = "roles", allEntries = true)
	@Scheduled(fixedDelay = EVICT_DELAY_MINUTES * 60 * 1000, initialDelay = 1000)
	public void evictCache()
	{
		log.debug("Role cache evicted");
	}

	@Bean
	public TaskScheduler taskScheduler()
	{
		return new ConcurrentTaskScheduler();
	}
}
