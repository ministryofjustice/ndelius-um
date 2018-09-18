package uk.co.bconline.ndelius.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig
{
	@Bean
	public CacheManager cacheManager()
	{
		return new ConcurrentMapCacheManager("roles", "roleGroups");
	}

	@CacheEvict(value = {"roles", "roleGroups"}, allEntries = true)
	public void evictCache()
	{
		log.debug("Role caches evicted");
	}
}
