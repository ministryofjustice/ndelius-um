package uk.co.bconline.ndelius.config.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
		return new ConcurrentMapCacheManager();
	}

	public void evictCache()
	{
		cacheManager().getCacheNames().parallelStream()
				.map(cacheManager()::getCache)
				.peek(cache -> log.debug("Cache {} = {}", cache.getName(), cache.getNativeCache()))
				.forEach(Cache::clear);
		log.debug("Flushed all caches");
	}
}
