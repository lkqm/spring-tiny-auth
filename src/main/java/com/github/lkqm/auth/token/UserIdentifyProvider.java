package com.github.lkqm.auth.token;

/**
 * 提供用户唯一标识, 用于token生成
 */
public interface UserIdentifyProvider {

    /**
     * 获取唯一标识别
     *
     * @return
     */
    String getUserIdentify();

}
