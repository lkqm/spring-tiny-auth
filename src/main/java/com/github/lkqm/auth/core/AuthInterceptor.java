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

    private static String[] excludeControllers = {
            "org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController",
            "org.springframework.boot.autoconfigure.web.BasicErrorController"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String method = request.getMethod();
            boolean isNeedTinyAuth = !isExcludeMethod(method) && !isExcludeController(handlerMethod);
            if (isNeedTinyAuth) {
                tinyAuth.auth(request, handlerMethod);
            }
        }
        return true;
    }

    private boolean isExcludeController(HandlerMethod handlerMethod) {
        String className = handlerMethod.getBeanType().getName();
        for (String exclude : excludeControllers) {
            if (exclude.equals(className)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExcludeMethod(String method) {
        return method.equalsIgnoreCase("OPTIONS");
    }
}
