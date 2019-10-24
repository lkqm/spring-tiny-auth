package com.github.lkqm.auth.core;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * 基于uri的权限资源数据
 */
@Getter
public class PatternPermission implements Serializable {

    /**
     * 请求路径与spring mvc路径一致
     */
    private String pattern;

    /**
     * 请求方法, 空代表所有请求, 多个请求逗号分隔
     */
    private String method;

    public PatternPermission(String pattern) {
        this.pattern = pattern;
    }

    public PatternPermission(String pattern, String method) {
        this.pattern = pattern;
        this.method = (method == null ? null : method.toUpperCase());
    }

    /**
     * 是否匹配指定地址和请求方法
     */
    public boolean match(String pattern, String requestMethod) {
        boolean isUriMatch = Objects.equals(this.pattern, pattern);
        return isUriMatch && (StringUtils.isEmpty(method) || method.indexOf(requestMethod.toUpperCase()) != -1);
    }
}
