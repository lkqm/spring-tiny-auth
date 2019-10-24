package com.github.lkqm.auth.core;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 相关账户的认证授权信息
 */
@Data
public class AuthInfo implements Serializable {

    /**
     * 是否登录过期
     */
    private boolean authExpired = false;

    /**
     * 是否是超级管理员(拥有所有权限)
     */
    private boolean superAdmin = false;

    /**
     * 角色
     */
    private List<String> roles;

    /**
     * 权限
     */
    private List<String> permissions;

    /**
     * 路径权限
     */
    private List<PatternPermission> patternPermissions;

    /**
     * 判断是否拥有指定请求的访问权限
     */
    public boolean hasPatternPermission(String pattern, String method) {
        if (!CollectionUtils.isEmpty(patternPermissions)) {
            for (PatternPermission patternPermission : patternPermissions) {
                if (patternPermission.match(pattern, method)) return true;
            }
        }
        return false;
    }
}
