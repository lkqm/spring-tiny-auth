package com.github.lkqm.auth;

import com.github.lkqm.auth.core.AuthExpressionRoot;
import com.github.lkqm.auth.core.AuthInfoProvider;
import com.github.lkqm.auth.core.AuthInterceptor;
import com.github.lkqm.auth.core.TinyAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 权限认证配置类
 */
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class AuthConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private AuthProperties authProperties;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private AuthInfoProvider authInfoProvider;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor());
    }

    /**
     * 权限认证拦截器
     */
    @Bean
    public AuthInterceptor authInterceptor() {
        AuthInterceptor authInterceptor = new AuthInterceptor(tinyAuth());
        return authInterceptor;
    }

    /**
     * 权限认证核心逻辑
     */
    @Bean
    @ConditionalOnMissingBean
    public TinyAuth tinyAuth() {
        TinyAuth tinyAuth = new TinyAuth(authInfoProvider, authProperties, authExpressionRoot(), applicationContext);
        return tinyAuth;
    }

    /**
     * 注解表达式根对象
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthExpressionRoot authExpressionRoot() {
        AuthExpressionRoot expressionRoot = new AuthExpressionRoot(authInfoProvider);
        return expressionRoot;
    }
}
