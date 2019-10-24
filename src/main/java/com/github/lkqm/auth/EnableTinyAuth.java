package com.github.lkqm.auth;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动认证
 *
 * @see AuthConfiguration 对应加载的配置类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AuthConfiguration.class)
public @interface EnableTinyAuth {
}
