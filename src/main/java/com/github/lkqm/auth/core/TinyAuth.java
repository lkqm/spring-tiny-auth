package com.github.lkqm.auth.core;

import com.github.lkqm.auth.AuthProperties;
import com.github.lkqm.auth.annotation.Auth;
import com.github.lkqm.auth.exception.AuthException;
import com.github.lkqm.auth.exception.AuthExpiredException;
import com.github.lkqm.auth.exception.AuthNotLoggedException;
import com.github.lkqm.auth.exception.AuthPermissionException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        Auth auth = Utils.getAuthAnnotation(handlerMethod);
        if (auth == null) {
            checkPatterns(request, handlerMethod);
        } else {
            checkAnnotation(auth);
        }
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

        if (authInfo !=null && authInfo.isSuperAdmin()) {
            return;
        }

        if (isAuthorPatterns(path)) {
            if (authInfo == null) throw new AuthNotLoggedException("未登录");
            if (authInfo.isAuthExpired()) throw new AuthExpiredException("登录过期");

            String pattern = Utils.getHandlerMethodPattern(applicationContext, pathMatcher, handlerMethod, path);
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

}
