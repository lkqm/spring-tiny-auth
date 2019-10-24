package com.github.lkqm.auth.core;

/**
 * 用户信息提供
 */
public interface AuthInfoProvider {

    /**
     * 提供用户信息
     * @return 当用户未登录返回null
     */
    AuthInfo doGetAuthInfo();

}
