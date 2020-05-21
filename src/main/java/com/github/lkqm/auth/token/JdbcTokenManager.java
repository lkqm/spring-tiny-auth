package com.github.lkqm.auth.token;

import com.github.lkqm.auth.token.support.JsonUtils;
import com.github.lkqm.auth.token.support.TokenInfo;
import com.github.lkqm.auth.token.support.TokenInfoRepository;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Store token data in relation database.
 */
@Getter
public class JdbcTokenManager implements TokenManager {

    /**
     * token有效期(分钟数)
     */
    private final int tokenLiveMinutes;

    /**
     * token剩余有效期小于这个值会刷新
     */
    private final int tokenDelayThreshold;
    private final TokenInfoRepository tokenRepository;

    public JdbcTokenManager(int tokenLiveMinutes, int tokenDelayThreshold, @NonNull TokenInfoRepository tokenRepository) {
        if (tokenLiveMinutes <= 0) {
            throw new IllegalArgumentException("tokenLiveMinutes > 0?:" + tokenLiveMinutes);
        }
        if (tokenDelayThreshold <= 0) {
            throw new IllegalArgumentException("tokenDelayThreshold > 0?:" + tokenDelayThreshold);
        }
        if (tokenDelayThreshold > tokenLiveMinutes) {
            throw new IllegalArgumentException("tokenLiveMinutes > tokenDelayThreshold?:" + tokenLiveMinutes + "," + tokenDelayThreshold);
        }
        this.tokenLiveMinutes = tokenLiveMinutes;
        this.tokenDelayThreshold = tokenDelayThreshold;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public String generateToken(Object userInfo) {
        String token = UUID.randomUUID().toString();
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(token);
        tokenInfo.setData(JsonUtils.toJson(userInfo));
        tokenInfo.setIssueTimestamp(System.currentTimeMillis());
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tokenLiveMinutes));
        tokenRepository.save(tokenInfo);
        return token;
    }

    @Override
    public void removeToken(String token) {
        tokenRepository.deleteById(token);
    }

    @Override
    public <T> T getTokenData(String token, Class<T> type) {
        return doGetUserInfoAndDelay(token, false, type);
    }

    @Override
    public <T> T getTokenDataAndDelay(String token, Class<T> type) {
        return doGetUserInfoAndDelay(token, true, type);
    }

    private <T> T doGetUserInfoAndDelay(String token, boolean delay, Class<T> type) {
        TokenInfo tokenInfo = tokenRepository.findById(token);
        if (tokenInfo == null) return null;
        if (tokenInfo.getExpireTimestamp() < System.currentTimeMillis()) {
            tokenRepository.deleteById(token);
            return null;
        }

        boolean needDelay = delay
                && tokenDelayThreshold >= TimeUnit.MILLISECONDS.toMinutes(tokenInfo.getExpireTimestamp() - System.currentTimeMillis());
        if (needDelay) {
            TokenInfo row = new TokenInfo();
            row.setToken(token);
            row.setExpireTimestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tokenLiveMinutes));
            tokenRepository.save(row);
        }
        return JsonUtils.fromJson(tokenInfo.getData(), type);
    }


}
