package com.github.lkqm.auth;

import com.github.lkqm.auth.token.*;
import com.github.lkqm.auth.token.support.TokenInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.JedisPool;

/**
 * 权限认证配置类
 */
@Configuration
@ConditionalOnProperty(name = "tiny-auth.token.type")
@AllArgsConstructor
public class TokenManagerConfiguration {

    private AuthProperties authProperties;
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public TokenManager tokenManager() {
        AuthProperties.TokenConfig tokenConfig = authProperties.getToken();
        AuthProperties.TokenType tokenType = tokenConfig.getType();
        if (tokenType == AuthProperties.TokenType.httpsession) {
            return new HttpSessionTokenManager(tokenConfig.getHttpSessionAttributeName());
        }
        if (tokenType == AuthProperties.TokenType.jdbc) {
            JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
            TokenInfoRepository tokenRepository = new TokenInfoRepository(tokenConfig.getJdbcTableName(), jdbcTemplate);
            return new JdbcTokenManager(tokenConfig.getLiveMinutes(), tokenConfig.getDelayThreshold(), tokenRepository);
        }
        if (tokenType == AuthProperties.TokenType.jedis) {
            JedisPool jedisPool = applicationContext.getBean(JedisPool.class);
            return new JedisTokenManager(tokenConfig.getLiveMinutes(), tokenConfig.getRedisKeyPrefix(), jedisPool);
        }
        if (tokenType == AuthProperties.TokenType.redistemplate) {
            StringRedisTemplate redisTemplate = applicationContext.getBean(StringRedisTemplate.class);
            return new RedisTemplateTokenManager(tokenConfig.getLiveMinutes(), tokenConfig.getRedisKeyPrefix(), redisTemplate);
        }

        throw new RuntimeException("无效的tiny-auth.token.type值");
    }
}
