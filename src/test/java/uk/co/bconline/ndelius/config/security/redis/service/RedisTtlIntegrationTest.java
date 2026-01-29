package uk.co.bconline.ndelius.config.security.redis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.bconline.ndelius.config.security.redis.repository.OAuth2AuthorizationGrantAuthorizationRepository;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.HOURS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static uk.co.bconline.ndelius.test.util.TokenUtils.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class RedisTtlIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private OAuth2AuthorizationGrantAuthorizationRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .alwaysDo(print())
            .build();
    }

    @Test
    public void clientCredentialsGrantHasTtlSetInRedis() throws Exception {
        String accessToken = clientCredentialsToken(mvc, "test.server.client");
        String id = repository.findByAccessToken_TokenValue(accessToken).getId();
        Long expire = redisTemplate.getExpire("oauth2_authorization:" + id, TimeUnit.SECONDS);
        assertExpiry(expire, 1);
    }

    @Test
    public void authorizationCodeGrantHasTtlSetInRedis() throws Exception {
        String accessToken = authCodeToken(mvc, "test.server.client");
        String id = repository.findByAccessToken_TokenValue(accessToken).getId();
        Long expire = redisTemplate.getExpire("oauth2_authorization:" + id, TimeUnit.SECONDS);
        assertExpiry(expire, 16);
    }

    @Test
    public void preAuthenticatedGrantHasTtlSetInRedis() throws Exception {
        String accessToken = preAuthenticatedToken(mvc, "test.server.client");
        String id = repository.findByAccessToken_TokenValue(accessToken).getId();
        Long expire = redisTemplate.getExpire("oauth2_authorization:" + id, TimeUnit.SECONDS);
        assertExpiry(expire, 16);
    }

    private static void assertExpiry(Long expire, int hours) {
        assertThat(expire, lessThanOrEqualTo(HOURS.toSeconds(hours)));
        assertThat(expire, greaterThan(HOURS.toSeconds(hours) - 30));
    }
}
