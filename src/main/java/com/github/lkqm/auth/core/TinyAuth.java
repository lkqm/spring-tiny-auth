package com.github.lkqm.auth.core;

import com.github.lkqm.auth.annotation.Auth;
import com.github.lkqm.auth.exception.AuthException;
import com.github.lkqm.auth.exception.AuthExpiredException;
import com.github.lkqm.auth.exception.AuthNotLoggedException;
import com.github.lkqm.auth.exception.AuthPermissionException;
import com.github.lkqm.auth.AuthProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 认证授权核心校验
 */
@AllArgsConstructor
public class TinyAuth {
    private AuthInfoProvider authInfoProvider;
    private AuthProperties authProperties;
    private AuthExpressionRoot expressionRoot;
    private ApplicationContext applicationContext;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher(AntPathMatcher.DEFAULT_PATH_SEPARATOR);

    /**
     * 检查认证授权
     *
     * @param request
     * @param handlerMethod
     * @throws AuthException
     */
    public void auth(HttpServletRequest request, HandlerMethod handlerMethod) throws AuthException {
        Auth auth = getAuthAnnotation(handlerMethod);
        if (auth == null) {
            checkPatterns(request, handlerMethod);
        } else {
            checkAnnotation(auth);
        }
    }

    private Auth getAuthAnnotation(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        Auth auth = AnnotationUtils.getAnnotation(method, Auth.class);
        if (auth == null) {
            Class<?> clazz = handlerMethod.getBeanType();
            auth = AnnotationUtils.getAnnotation(clazz, Auth.class);
        }
        return auth;
    }

    private void checkAnnotation(Auth auth) {
        String expression = auth.value() == null ? "" : auth.value().trim();
        if (StringUtils.isEmpty(expression)) {
            expression = AuthExpressionRoot.AUTHEN_EXPRESSION;
        }
        boolean isPassed = ExpressionCheckUtils.check(new StandardEvaluationContext(expressionRoot), expression);
        if (!isPassed) {
            if (expression.startsWith(AuthExpressionRoot.AUTHEN_EXPRESSION_PREFIX)) {
                throw new AuthNotLoggedException("未登录");
            } else {
                throw new AuthPermissionException("无访问权限");
            }
        }
    }

    private void checkPatterns(HttpServletRequest request, HandlerMethod handlerMethod) {
        String method = request.getMethod();
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (isAnonPatterns(path)) return;

        AuthInfo authInfo = authInfoProvider.doGetAuthInfo();
        if (isAuthenPatterns(path)) {
            if (authInfo == null) throw new AuthNotLoggedException("未登录");
            if (authInfo.isAuthExpired()) throw new AuthExpiredException("登录过期");
            return;
        }

        if (authInfo.isSuperAdmin()) {
            return;
        }

        if (isAuthorPatterns(path)) {
            if (authInfo == null) throw new AuthNotLoggedException("未登录");
            if (authInfo.isAuthExpired()) throw new AuthExpiredException("登录过期");

            String pattern = getHandlerMethodPattern(handlerMethod, path);
            boolean result = authInfo.hasPatternPermission(pattern, method);
            if (!result) throw new AuthPermissionException("无权限");
        }
    }

    private boolean isAnonPatterns(String path) {
        return doMatchPattern(authProperties.getAnnoPatterns(), path);
    }

    private boolean isAuthenPatterns(String path) {
        return doMatchPattern(authProperties.getAuthenPatterns(), path);
    }

    private boolean isAuthorPatterns(String path) {
        return doMatchPattern(authProperties.getAuthorPatterns(), path);
    }

    private boolean doMatchPattern(List<String> patterns, String path) {
        if (!CollectionUtils.isEmpty(patterns)) {
            for (String pattern : patterns) {
                if (pathMatcher.match(pattern, path)) return true;
            }
        }
        return false;
    }

    private String getHandlerMethodPattern(HandlerMethod handler, String path) {
        RequestMappingHandlerMapping handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        RequestMappingInfo requestMappingInfo = handlerMapping.getHandlerMethods().entrySet()
                .stream().filter(entry -> entry.getValue().getMethod().equals(handler.getMethod()))
                .map(Map.Entry::getKey).findFirst().orElse(null);
        if (requestMappingInfo == null) {
            throw new IllegalStateException("Assert requestMappingInfo != null for handler: " + handler);
        }
        Set<String> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
        if (CollectionUtils.isEmpty(patterns)) {
            throw new IllegalStateException("Assert (patterns != null && patterns.size() != 0) for handler: " + handler);
        }
        if (patterns.size() == 1) {
            return patterns.iterator().next();
        }
        String pattern = patterns.stream().filter(p -> pathMatcher.match(p, path)).findFirst().orElse(null);
        if (pattern == null) {
            throw new IllegalStateException("Assert (pattern != null) for handler: " + handler);
        }
        return pattern;
    }

}