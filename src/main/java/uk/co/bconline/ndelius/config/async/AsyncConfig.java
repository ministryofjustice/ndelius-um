package uk.co.bconline.ndelius.config.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Slf4j
@Configuration
public class AsyncConfig {
	@Bean
	public ThreadPoolTaskExecutor applicationTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
		return builder.build();
	}

	/*
	 * Create a DelegatingSecurityContextAsyncTaskExecutor bean. This is used for asynchronous calls where the Security
	 * Context is required in child threads, for example when setting the created/updated details when saving changes to
	 * a user.
	 *
	 * Note: This approach is used in favour of the MODE_INHERITABLETHREADLOCAL context holder strategy, due to issues
	 * around using pooled task executors. See https://github.com/spring-projects/spring-security/issues/6856
	 */
	@Bean
	public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(ThreadPoolTaskExecutor delegate) {
		return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
	}
}
