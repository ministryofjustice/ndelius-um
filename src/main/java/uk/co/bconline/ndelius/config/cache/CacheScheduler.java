package uk.co.bconline.ndelius.config.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Slf4j
@Configuration
@Profile("!test")
@EnableScheduling
public class CacheScheduler
{
	private final CacheConfig cacheConfig;

	@Autowired
	public CacheScheduler(CacheConfig cacheConfig)
	{
		this.cacheConfig = cacheConfig;
	}

	@Scheduled(fixedDelayString = "${cache.evict.delay}", initialDelay = 1000)
	public void evictSchedule()
	{
		cacheConfig.evictCache();
	}

	@Bean
	public TaskScheduler taskScheduler()
	{
		return new ConcurrentTaskScheduler();
	}
}
