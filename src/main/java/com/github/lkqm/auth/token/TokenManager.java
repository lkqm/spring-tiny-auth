package com.github.lkqm.auth.token;

/**
 * 管理员Token管理
 */
public interface TokenManager {

    /**
     * 保存token
     */
    String generateToken(Object data);

    /**
     * 移除token
     */
    void removeToken(String token);

    /**
     * 根据token获取数据
     */
    <T> T getTokenData(String token, Class<T> type);

    /**
     * 获取token数据, 并延长有效期
     */
    <T> T getTokenDataAndDelay(String token, Class<T> type);
}
