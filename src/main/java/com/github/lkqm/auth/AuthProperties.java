package com.github.lkqm.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;


@Data
@ConfigurationProperties("tiny-auth")
public class AuthProperties implements Serializable {

    /**
     * 允许匿名访问的路径(支持ant风格)
     */
    private List<String> annoPatterns;

    /**
     * 只需要登录的路径
     */
    private List<String> authenPatterns;

    /**
     * 拦截的路径(支持ant风格)
     */
    private List<String> authorPatterns;

}
