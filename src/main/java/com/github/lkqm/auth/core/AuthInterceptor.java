package com.github.lkqm.auth.core;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限拦截器
 */
@AllArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private TinyAuth tinyAuth;

    private static final Class<?>[] excludeControllers = {BasicErrorController.class};

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
        Class<?> target = handlerMethod.getBeanType();
        for (Class<?> exclude : excludeControllers) {
            if (exclude == target) {
                return true;
            }
        }
        return false;
    }

    private boolean isExcludeMethod(String method) {
        return method.equalsIgnoreCase("OPTIONS");
    }
}
