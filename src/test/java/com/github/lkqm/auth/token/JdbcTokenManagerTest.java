package com.github.lkqm.auth.token;

import com.github.lkqm.auth.token.support.JsonUtils;
import com.github.lkqm.auth.token.support.TokenInfo;
import com.github.lkqm.auth.token.support.TokenInfoRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JdbcTokenManagerTest {

    TokenInfoRepository tokenRepository;
    JdbcTokenManager tokenManager;

    @Before
    public void init() {
        tokenRepository = mock(TokenInfoRepository.class);
        tokenManager = new JdbcTokenManager(30, 20, tokenRepository);
    }

    @Test
    public void generateToken() {
        ArgumentCaptor<TokenInfo> tokeInfoCaptor = ArgumentCaptor.forClass(TokenInfo.class);
        String token = tokenManager.generateToken("data");
        verify(tokenRepository).save(tokeInfoCaptor.capture());

        TokenInfo tokenInfo = tokeInfoCaptor.getValue();
        assertNotNull(tokenInfo);
        assertEquals(token, tokenInfo.getToken());
        assertEquals(JsonUtils.toJson("data"), tokenInfo.getData());

        long diff = TimeUnit.MILLISECONDS.toMinutes(tokenInfo.getExpireTimestamp() - tokenInfo.getIssueTimestamp());
        assertTrue(diff >= 29 && diff <= 31);
    }

    @Test
    public void removeToken() {
        tokenManager.removeToken("110");
        verify(tokenRepository).deleteById(anyString());
    }

    @Test
    public void getTokenData() {
        TokenInfo tokenInfo = mock(TokenInfo.class);
        when(tokenInfo.getToken()).thenReturn("123");
        when(tokenInfo.getData()).thenReturn("123");
        when(tokenInfo.getIssueTimestamp()).thenReturn(System.currentTimeMillis());
        when(tokenInfo.getExpireTimestamp()).thenReturn(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10));
        when(tokenRepository.findById(anyString())).thenReturn(tokenInfo);
        Integer tokenData = tokenManager.getTokenData(tokenInfo.getToken(), Integer.class);
        assertEquals(Integer.valueOf(123), tokenData);

        // token过期
        when(tokenInfo.getExpireTimestamp()).thenReturn(System.currentTimeMillis() - 10000);
        tokenData = tokenManager.getTokenData(tokenInfo.getToken(), Integer.class);
        assertNull(tokenData);
    }

    @Test
    public void getTokenDataAndDelay() {
        TokenInfo tokenInfo = mock(TokenInfo.class);
        when(tokenInfo.getToken()).thenReturn("123");
        when(tokenInfo.getData()).thenReturn("123");
        when(tokenInfo.getIssueTimestamp()).thenReturn(System.currentTimeMillis());
        when(tokenInfo.getExpireTimestamp()).thenReturn(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10));
        when(tokenRepository.findById(anyString())).thenReturn(tokenInfo);
        tokenManager.getTokenDataAndDelay(tokenInfo.getToken(), Integer.class);
        verify(tokenRepository).save(any(TokenInfo.class));
    }
}