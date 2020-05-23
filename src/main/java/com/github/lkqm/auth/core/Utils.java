package com.github.lkqm.auth.core;

import com.github.lkqm.auth.annotation.Auth;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class Utils {


    /**
     * 获取Auth注解
     */
    public static Auth getAuthAnnotation(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        Auth auth = AnnotationUtils.getAnnotation(method, Auth.class);
        if (auth == null) {
            auth = method.getAnnotation(Auth.class);
        }

        if (auth == null) {
            Class<?> clazz = handlerMethod.getBeanType();
            auth = AnnotationUtils.getAnnotation(clazz, Auth.class);
            if (auth == null) {
                auth = clazz.getAnnotation(Auth.class);
            }
        }
        return auth;
    }


    /**
     * 获取控制器处理方法上匹配的路径
     */
    public static String getHandlerMethodPattern(ApplicationContext applicationContext, AntPathMatcher pathMatcher, HandlerMethod handler, String path) {
        RequestMappingInfo requestMappingInfo = null;
        RequestMappingHandlerMapping handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Set<Map.Entry<RequestMappingInfo, HandlerMethod>> entries = handlerMapping.getHandlerMethods().entrySet();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : entries) {
            RequestMappingInfo oneMappingInfo = entry.getKey();
            HandlerMethod oneHandlerMethod = entry.getValue();
            if (oneHandlerMethod.getMethod().equals(handler.getMethod())) {
                requestMappingInfo = oneMappingInfo;
                break;
            }
        }
        if (requestMappingInfo == null) {
            throw new IllegalStateException("Assert requestMappingInfo != null for handler: " + handler);
        }

        // 匹配的路径
        Set<String> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
        if (CollectionUtils.isEmpty(patterns)) {
            throw new IllegalStateException("Assert (patterns != null && patterns.size() != 0) for handler: " + handler);
        }
        if (patterns.size() == 1) {
            return patterns.iterator().next();
        }

        String pattern = null;
        Iterator<String> iterator = patterns.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (pathMatcher.match(next, path)) {
                pattern = next;
                break;
            }
        }
        if (pattern == null) {
            throw new IllegalStateException("Assert (pattern != null) for handler: " + handler);
        }
        return pattern;
    }
}
