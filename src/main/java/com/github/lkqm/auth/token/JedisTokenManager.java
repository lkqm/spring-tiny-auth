package com.github.lkqm.auth.token;

import com.github.lkqm.auth.token.support.JsonUtils;
import lombok.NonNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.Pool;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Store token data in redis by Jedis client.
 */
public class JedisTokenManager implements TokenManager {

    private final int tokenLiveMinutes;
    private final String keyPrefix;
    private final Pool<Jedis> jedisPool;

    public JedisTokenManager(int tokenLiveMinutes, Pool<Jedis> jedisPool) {
        this(tokenLiveMinutes, "tiny-auth", jedisPool);
    }

    public JedisTokenManager(int tokenLiveMinutes, String keyPrefix, @NonNull Pool<Jedis> jedisPool) {
        if (tokenLiveMinutes <= 0) {
            throw new IllegalArgumentException("tokenLiveMinutes > 0?:" + tokenLiveMinutes);
        }
        if (keyPrefix == null || keyPrefix.trim().length() == 0) {
            throw new IllegalArgumentException("keyPrefix must not be empty");
        }
        this.tokenLiveMinutes = tokenLiveMinutes;
        this.keyPrefix = keyPrefix.trim();
        this.jedisPool = jedisPool;
    }

    @Override
    public String generateToken(Object data) {
        String token = UUID.randomUUID().toString();
        String key = doGetKey(token);
        String value = JsonUtils.toJson(data);
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.setex(key, (int) TimeUnit.MINUTES.toSeconds(tokenLiveMinutes), value);
        }
        return token;
    }

    @Override
    public void removeToken(String token) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.del(doGetKey(token));
        }
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
        try (Jedis jedis = this.jedisPool.getResource()) {
            String key = doGetKey(token);
            String value = jedis.get(key);
            if (value == null || value.length() == 0) return null;
            if (delay) {
                jedis.setex(key, (int) TimeUnit.MINUTES.toSeconds(tokenLiveMinutes), value);
            }
            return JsonUtils.fromJson(value, type);
        }
    }


    private String doGetKey(String token) {
        return keyPrefix + ":token:" + token;
    }
}
