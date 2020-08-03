package uk.co.bconline.ndelius.test.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class TokenCache {
	static Map<String, String> TOKEN_CACHE = new ConcurrentHashMap<>();

	@EventListener(ContextRefreshedEvent.class)
	public void clearTokenCache() {
		TOKEN_CACHE.clear();
	}
}