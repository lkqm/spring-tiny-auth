package com.github.lkqm.auth.core;

import lombok.AllArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限拦截器
 */
@AllArgsConstructor
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private TinyAuth tinyAuth;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String method = request.getMethod();
            boolean isNeedTinyAuth = !isExcludeMethod(method);
            if (isNeedTinyAuth) {
                tinyAuth.auth(request, handlerMethod);
            }
        }
        return true;
    }

    private boolean isExcludeMethod(String method) {
        return method.equalsIgnoreCase("OPTIONS");
    }
}
