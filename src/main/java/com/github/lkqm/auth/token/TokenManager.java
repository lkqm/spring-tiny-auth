package com.github.lkqm.auth.token;

/**
 * 管理员Token管理
 */
public interface TokenManager<T> {

    /**
     * 保存token
     *
     * @param userInfo
     */
    String generateAndSaveToken(T userInfo);

    /**
     * 移除token
     *
     * @param token
     * @return
     */
    T removeToken(String token);

    /**
     * 获取用户信息, 根据token
     *
     * @param token
     * @return
     */
    T getUserInfo(String token);

    /**
     * 获取用户信息, 并延长token过期时间
     *
     * @param token
     * @return
     */
    T getUserInfoAndDelay(String token);
}
