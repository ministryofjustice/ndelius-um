package uk.co.bconline.ndelius.config.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.bconline.ndelius.service.RoleGroupService;
import uk.co.bconline.ndelius.service.RoleService;

import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {
    private final RoleService roleService;
    private final RoleGroupService roleGroupService;

    @Autowired
    public CacheConfig(RoleService roleService, RoleGroupService roleGroupService) {
        this.roleService = roleService;
        this.roleGroupService = roleGroupService;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    public void evictCache() {
        getCaches().forEach(Cache::clear);
        log.info("Flushed all caches");

        // Populate role + rolegroup cache
        roleService.getAllRoles();
        roleGroupService.getRoleGroups().forEach(group -> roleGroupService.getRoleGroup(group.getName()));
        log.info("Re-populated role and group caches");
        getCaches();
    }

    private Stream<Cache> getCaches() {
        return cacheManager().getCacheNames().parallelStream()
            .map(cacheManager()::getCache)
            .filter(Objects::nonNull)
            .peek(cache -> log.debug("Cache {} = {}", cache.getName(), cache.getNativeCache()));
    }
}
