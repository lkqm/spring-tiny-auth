package com.github.lkqm.auth.token;

import com.github.lkqm.auth.token.support.JsonUtils;
import lombok.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Store token data in redis by RedisTemplate.
 */
public class RedisTemplateTokenManager implements TokenManager {

    private final int tokenLiveMinutes;
    private final String keyPrefix;
    private final StringRedisTemplate redisTemplate;

    public RedisTemplateTokenManager(int tokenLiveMinutes, StringRedisTemplate redisTemplate) {
        this(tokenLiveMinutes, "tiny-auth", redisTemplate);
    }

    public RedisTemplateTokenManager(int tokenLiveMinutes, @NonNull String keyPrefix, @NonNull StringRedisTemplate redisTemplate) {
        if (tokenLiveMinutes <= 0) {
            throw new IllegalArgumentException("tokenLiveMinutes > 0?:" + tokenLiveMinutes);
        }
        if (keyPrefix == null || keyPrefix.trim().length() == 0) {
            throw new IllegalArgumentException("keyPrefix must not be empty");
        }
        this.tokenLiveMinutes = tokenLiveMinutes;
        this.keyPrefix = keyPrefix.trim();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateToken(Object data) {
        String token = UUID.randomUUID().toString();
        String key = doGetKey(token);
        String value = JsonUtils.toJson(data);
        redisTemplate.opsForValue().set(key, value, tokenLiveMinutes, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public void removeToken(String token) {
        redisTemplate.delete(doGetKey(token));
    }

    @Override
    public <T> T getTokenData(String token, Class<T> type) {
        return doGetUserInfoAndDelay(token, false, type);
    }

    @Override
    public <T> T getTokenDataAndDelay(String token, Class<T> type) {
        return doGetUserInfoAndDelay(token, true, type);
    }

    public <T> T doGetUserInfoAndDelay(String token, boolean delay, Class<T> type) {
        String key = doGetKey(token);
        String value = redisTemplate.opsForValue().get(key);
        if (value == null || value.length() == 0) return null;
        if (delay) {
            redisTemplate.opsForValue().set(key, value, tokenLiveMinutes, TimeUnit.MINUTES);
        }
        return JsonUtils.fromJson(value, type);
    }


    private String doGetKey(String token) {
        return keyPrefix + ":token:" + token;
    }
}
