package com.github.lkqm.auth.core;

import com.github.lkqm.auth.annotation.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 认证授权表达式根对象
 *
 * @see Auth
 */
@Getter
@AllArgsConstructor
public class AuthExpressionRoot {

    protected AuthInfoProvider authProvider;

    public static final String AUTHEN_EXPRESSION = "authen()";
    public static final String AUTHEN_EXPRESSION_PREFIX = "authen";

    /**
     * 允许匿名访问
     */
    public final boolean anno() {
        return true;
    }

    /**
     * 需要登录(认证)
     */
    public final boolean authen() {
        AuthInfo authInfo = getAuthInfo();
        return isAuthen(authInfo);
    }

    /**
     * 是否拥有任意一个指定的角色
     */
    public final boolean hasRole(String... roles) {
        AuthInfo authInfo = getAuthInfo();
        if (!isAuthen(authInfo)) return false;
        if (authInfo.isSuperAdmin()) return true;

        List<String> userRoles = authInfo.getRoles();
        if (userRoles != null) {
            return CollectionUtils.containsAny(userRoles, Arrays.asList(roles));
        }
        return false;
    }

    /**
     * 是否拥有指定的所有角色
     */
    public final boolean hasAllRole(String... roles) {
        AuthInfo authInfo = getAuthInfo();
        if (!isAuthen(authInfo)) return false;
        if (authInfo.isSuperAdmin()) return true;

        List<String> userRoles = authInfo.getRoles();
        if (userRoles != null) {
            return userRoles.containsAll(Arrays.asList(roles));
        }
        return false;
    }

    /**
     * 是否拥有任意一个指定的权限
     */
    public final boolean hasPermission(String... permissions) {
        AuthInfo authInfo = getAuthInfo();
        if (!isAuthen(authInfo)) return false;
        if (authInfo.isSuperAdmin()) return true;

        List<String> userPermissions = authInfo.getPermissions();
        if (userPermissions != null) {
            return CollectionUtils.containsAny(userPermissions, Arrays.asList(permissions));
        }
        return false;
    }

    /**
     * 是否拥有指定的所有权限
     */
    public final boolean hasAllPermission(String... permissions) {
        AuthInfo authInfo = getAuthInfo();
        if (!isAuthen(authInfo)) return false;
        if (authInfo.isSuperAdmin()) return true;

        List<String> userPermissions = authInfo.getPermissions();
        if (userPermissions != null) {
            return userPermissions.containsAll(Arrays.asList(permissions));
        }
        return false;
    }

    private AuthInfo getAuthInfo() {
        return authProvider.doGetAuthInfo();
    }

    private boolean isAuthen(AuthInfo authInfo) {
        if (authInfo == null || authInfo.isAuthExpired()) {
            return false;
        }
        return true;
    }

}
