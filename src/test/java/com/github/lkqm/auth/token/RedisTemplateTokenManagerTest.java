package com.github.lkqm.auth.token;

import com.github.lkqm.auth.token.support.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RedisTemplateTokenManagerTest {

    StringRedisTemplate redisTemplate;
    ValueOperations<String, String> valueOperations;
    RedisTemplateTokenManager tokenManager;

    @Before
    public void init() {
        this.redisTemplate = mock(StringRedisTemplate.class);
        this.tokenManager = new RedisTemplateTokenManager(30, redisTemplate);
        this.valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void generateToken() {
        String token = tokenManager.generateToken("data");

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(keyCaptor.capture(), valueCaptor.capture(), anyLong(), any(TimeUnit.class));

        assertEquals("tiny-auth:token:" + token, keyCaptor.getValue());
        assertEquals(JsonUtils.toJson("data"), valueCaptor.getValue());
    }

    @Test
    public void removeToken() {
        String token = UUID.randomUUID().toString();
        tokenManager.removeToken(token);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(redisTemplate).delete(keyCaptor.capture());
        assertEquals("tiny-auth:token:" + token, keyCaptor.getValue());
    }

    @Test
    public void getTokenData() {
        String token = "123456", key = "tiny-auth:token:" + token;
        when(valueOperations.get(key)).thenReturn("110");
        Integer data = tokenManager.getTokenData("123456", Integer.class);
        assertEquals(Integer.valueOf(110), data);
    }

    @Test
    public void getTokenDataAndDelay() {
        String token = "123456", key = "tiny-auth:token:" + token;
        when(valueOperations.get(key)).thenReturn("110");
        Integer data = tokenManager.getTokenDataAndDelay("123456", Integer.class);
        assertEquals(Integer.valueOf(110), data);
        verify(valueOperations, times(1)).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }
}