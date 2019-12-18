package uk.co.bconline.ndelius.config.data.embedded;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Component
@ConditionalOnProperty("spring.redis.embedded")
public class EmbeddedRedisServer {

	@Value("${spring.redis.port}")
	private int redisPort;

	private RedisServer redisServer;

	@PostConstruct
	public void startRedis() throws IOException {
		redisServer = new RedisServer(redisPort);
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		redisServer.stop();
	}
}