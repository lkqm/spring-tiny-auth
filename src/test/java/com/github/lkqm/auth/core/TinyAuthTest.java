package com.github.lkqm.auth.core;

import com.github.lkqm.auth.AuthProperties;
import com.github.lkqm.auth.core.demo.AnnotationController;
import com.github.lkqm.auth.core.demo.PatternController;
import com.github.lkqm.auth.exception.AuthExpiredException;
import com.github.lkqm.auth.exception.AuthNotLoggedException;
import com.github.lkqm.auth.exception.AuthPermissionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class TinyAuthTest {
    @Mock
    AuthInfoProvider authInfoProvider;
    @Mock
    AuthProperties authProperties;
    @Mock
    ApplicationContext applicationContext;

    TinyAuth tinyAuth;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AuthExpressionRoot authExpressionRoot = new AuthExpressionRoot(authInfoProvider);
        this.tinyAuth = new TinyAuth(authInfoProvider, authProperties, authExpressionRoot, applicationContext);

        when(authProperties.getAnnoPatterns()).thenReturn(Arrays.asList("/auth/login", "/auth/logout"));
        when(authProperties.getAuthenPatterns()).thenReturn(Arrays.asList("/admin/info", "/admin/menu"));
        when(authProperties.getAuthorPatterns()).thenReturn(Arrays.asList("/**"));
    }

    /**
     * 测试正常逻辑下的注解
     */
    @Test
    public void testAuthAnnotation() throws NoSuchMethodException {
        AnnotationController controller = new AnnotationController();
        HttpServletRequest request = mock(HttpServletRequest.class);

        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuthExpired(false);
        authInfo.setPermissions(Arrays.asList("project:add", "project:update"));
        authInfo.setRoles(Arrays.asList("admin", "developer"));
        when(authInfoProvider.doGetAuthInfo()).thenReturn(authInfo);

        tinyAuth.auth(request, new HandlerMethod(controller, "login"));
        tinyAuth.auth(request, new HandlerMethod(controller, "menu"));
        tinyAuth.auth(request, new HandlerMethod(controller, "info"));
        tinyAuth.auth(request, new HandlerMethod(controller, "addProject"));
        tinyAuth.auth(request, new HandlerMethod(controller, "updateProject"));
    }

    /**
     * 测试注解未登录情况下
     */
    @Test(expected = AuthNotLoggedException.class)
    public void testAuthAnnotationAuthen1() throws NoSuchMethodException {
        AnnotationController controller = new AnnotationController();
        when(authInfoProvider.doGetAuthInfo()).thenReturn(null);
        tinyAuth.auth(mock(HttpServletRequest.class), new HandlerMethod(controller, "menu"));
    }

    /**
     * 测试注解登录过期情况下
     */
    @Test(expected = AuthNotLoggedException.class)
    public void testAuthAnnotationAuthen2() throws NoSuchMethodException {
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuthExpired(true);
        when(authInfoProvider.doGetAuthInfo()).thenReturn(authInfo);

        AnnotationController controller = new AnnotationController();
        tinyAuth.auth(mock(HttpServletRequest.class), new HandlerMethod(controller, "menu"));
    }

    /**
     * 测试注解无角色情况下
     */
    @Test(expected = AuthPermissionException.class)
    public void testAuthAnnotationRole() throws NoSuchMethodException {
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuthExpired(true);
        when(authInfoProvider.doGetAuthInfo()).thenReturn(authInfo);

        AnnotationController controller = new AnnotationController();
        tinyAuth.auth(mock(HttpServletRequest.class), new HandlerMethod(controller, "addProject"));
    }

    /**
     * 测试注解无权限情况下
     */
    @Test(expected = AuthPermissionException.class)
    public void testAuthAnnotationPermission() throws NoSuchMethodException {
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuthExpired(true);
        when(authInfoProvider.doGetAuthInfo()).thenReturn(authInfo);

        AnnotationController controller = new AnnotationController();
        tinyAuth.auth(mock(HttpServletRequest.class), new HandlerMethod(controller, "updateProject"));
    }

    /**
     * 测试路径权限正常情况下
     */
    @Test
    public void testAuthPattern() throws NoSuchMethodException {
        PatternController controller = new PatternController();
        HttpServletRequest request = mock(HttpServletRequest.class);
        AuthInfo authInfo = new AuthInfo();
        authInfo.setPatternPermissions(new ArrayList<PatternPermission>() {{
            add(new PatternPermission("/project/add", "post"));
            add(new PatternPermission("/project/update", "post,put"));
            add(new PatternPermission("/project/delete/{id}", "post,delete"));
        }});
        when(authInfoProvider.doGetAuthInfo()).thenReturn(authInfo);
        when(request.getContextPath()).thenReturn("");
        when(request.getMethod()).thenReturn("post");

        PowerMockito.mockStatic(Utils.class);
        when(request.getRequestURI()).thenReturn("/auth/login");
        mockUtilsGetHandlerMethodPattern("/auth/login");
        tinyAuth.auth(request, new HandlerMethod(controller, "login"));

        when(request.getRequestURI()).thenReturn("/project/add");
        mockUtilsGetHandlerMethodPattern("/project/add");
        tinyAuth.auth(request, new HandlerMethod(controller, "addProject"));

        when(request.getRequestURI()).thenReturn("/project/update");
        mockUtilsGetHandlerMethodPattern("/project/update");
        tinyAuth.auth(request, new HandlerMethod(controller, "updateProject"));

        when(request.getRequestURI()).thenReturn("/project/delete/1");
        mockUtilsGetHandlerMethodPattern("/project/delete/{id}");
        tinyAuth.auth(request, new HandlerMethod(controller, "deleteProject"));
    }

    /**
     * 测试路径未登录
     */
    @Test(expected = AuthNotLoggedException.class)
    public void testAuthPatternAuthen1() throws NoSuchMethodException {
        PowerMockito.mockStatic(Utils.class);

        PatternController controller = new PatternController();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(authInfoProvider.doGetAuthInfo()).thenReturn(null);
        when(request.getContextPath()).thenReturn("");
        when(request.getMethod()).thenReturn("post");

        when(request.getRequestURI()).thenReturn("/project/add");
        mockUtilsGetHandlerMethodPattern("/project/add");
        tinyAuth.auth(request, new HandlerMethod(controller, "addProject"));
    }

    /**
     * 测试路径登录过期
     */
    @Test(expected = AuthExpiredException.class)
    public void testAuthPatternAuthen2() throws NoSuchMethodException {
        PowerMockito.mockStatic(Utils.class);

        PatternController controller = new PatternController();
        HttpServletRequest request = mock(HttpServletRequest.class);
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuthExpired(true);
        when(authInfoProvider.doGetAuthInfo()).thenReturn(authInfo);
        when(request.getContextPath()).thenReturn("");
        when(request.getMethod()).thenReturn("post");

        when(request.getRequestURI()).thenReturn("/project/add");
        mockUtilsGetHandlerMethodPattern("/project/add");
        tinyAuth.auth(request, new HandlerMethod(controller, "addProject"));
    }

    /**
     * 测试路径无权限
     */
    @Test(expected = AuthPermissionException.class)
    public void testAuthPatternPermission1() throws NoSuchMethodException {
        PowerMockito.mockStatic(Utils.class);

        PatternController controller = new PatternController();
        HttpServletRequest request = mock(HttpServletRequest.class);
        AuthInfo authInfo = new AuthInfo();
        when(authInfoProvider.doGetAuthInfo()).thenReturn(authInfo);
        when(request.getContextPath()).thenReturn("");
        when(request.getMethod()).thenReturn("post");

        when(request.getRequestURI()).thenReturn("/project/add");
        mockUtilsGetHandlerMethodPattern("/project/add");
        tinyAuth.auth(request, new HandlerMethod(controller, "addProject"));
    }

    /**
     * 测试路径无权限(rest)
     */
    @Test(expected = AuthPermissionException.class)
    public void testAuthPatternPermission2() throws NoSuchMethodException {
        PowerMockito.mockStatic(Utils.class);

        PatternController controller = new PatternController();
        HttpServletRequest request = mock(HttpServletRequest.class);
        AuthInfo authInfo = new AuthInfo();
        authInfo.setPatternPermissions(new ArrayList<PatternPermission>() {{
            add(new PatternPermission("/project/delete/{idx}", "post,delete"));
        }});
        when(authInfoProvider.doGetAuthInfo()).thenReturn(authInfo);
        when(request.getContextPath()).thenReturn("");
        when(request.getMethod()).thenReturn("post");

        when(request.getRequestURI()).thenReturn("/project/delete/1");
        mockUtilsGetHandlerMethodPattern("/project/delete/{id}");
        tinyAuth.auth(request, new HandlerMethod(controller, "deleteProject"));
    }


    private void mockUtilsGetHandlerMethodPattern(String pattern) {
        PowerMockito.when(
                Utils.getHandlerMethodPattern(any(ApplicationContext.class), any(AntPathMatcher.class), any(HandlerMethod.class), anyString())
        ).thenReturn(pattern);
    }
}

