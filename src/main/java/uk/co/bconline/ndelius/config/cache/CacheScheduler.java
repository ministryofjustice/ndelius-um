package uk.co.bconline.ndelius.config.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
public class CacheScheduler
{
	private static final int EVICT_DELAY_MS = 6 * 60 * 60 * 1000;	// 6 hours

	private final CacheManager cacheManager;
	private final CacheConfig cacheConfig;

	@Autowired
	public CacheScheduler(CacheManager cacheManager, CacheConfig cacheConfig)
	{
		this.cacheManager = cacheManager;
		this.cacheConfig = cacheConfig;
	}

	@Scheduled(fixedDelay = EVICT_DELAY_MS, initialDelay = 1000)
	public void evictSchedule()
	{
		log.debug("Role cache:  {}", cacheManager.getCache("roles").getNativeCache());
		log.debug("Group cache: {}", cacheManager.getCache("roleGroups").getNativeCache());
		cacheConfig.evictCache();
	}

	@Bean
	public TaskScheduler taskScheduler()
	{
		return new ConcurrentTaskScheduler();
	}
}
