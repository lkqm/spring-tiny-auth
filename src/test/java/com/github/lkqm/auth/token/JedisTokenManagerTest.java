package com.github.lkqm.auth.token;

import com.github.lkqm.auth.token.support.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class JedisTokenManagerTest {

    Jedis jedis;
    JedisTokenManager tokenManager;

    @Before
    public void init() {
        this.jedis = mock(Jedis.class);
        JedisPool jedisPool = mock(JedisPool.class);
        this.tokenManager = new JedisTokenManager(30, jedisPool);

        when(jedisPool.getResource()).thenReturn(this.jedis);
    }

    @Test
    public void generateToken() {
        String token = tokenManager.generateToken("data");

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> secondsCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        verify(jedis).setex(keyCaptor.capture(), secondsCaptor.capture(), valueCaptor.capture());

        assertEquals("tiny-auth:token:" + token, keyCaptor.getValue());
        assertEquals(Integer.valueOf(30 * 60), secondsCaptor.getValue());
        assertEquals(JsonUtils.toJson("data"), valueCaptor.getValue());
    }

    @Test
    public void removeToken() {
        String token = UUID.randomUUID().toString();
        tokenManager.removeToken(token);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(jedis).del(keyCaptor.capture());
        assertEquals("tiny-auth:token:" + token, keyCaptor.getValue());
    }

    @Test
    public void getTokenData() {
        String token = "123456", key = "tiny-auth:token:" + token;
        when(jedis.get(key)).thenReturn("110");
        Integer data = tokenManager.getTokenData("123456", Integer.class);
        assertEquals(Integer.valueOf(110), data);
    }

    @Test
    public void getTokenDataAndDelay() {
        String token = "123456", key = "tiny-auth:token:" + token;
        when(jedis.get(key)).thenReturn("110");
        Integer data = tokenManager.getTokenDataAndDelay("123456", Integer.class);
        assertEquals(Integer.valueOf(110), data);
        verify(jedis, times(1)).setex(anyString(), anyInt(), anyString());
    }

}