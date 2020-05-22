package com.github.lkqm.auth.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.Mockito.when;

public class AuthExpressionRootTest {
    @Mock
    AuthInfoProvider authProvider;
    @InjectMocks
    AuthExpressionRoot authExpressionRoot;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAnno() throws Exception {
        boolean result = authExpressionRoot.anno();
        Assert.assertEquals(true, result);
    }

    @Test
    public void testAuthen() throws Exception {
        when(authProvider.doGetAuthInfo()).thenReturn(new AuthInfo());

        boolean result = authExpressionRoot.authen();
        Assert.assertEquals(true, result);
    }

    @Test
    public void testHasRole() throws Exception {
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuthExpired(false);
        authInfo.setRoles(Arrays.asList("admin", "developer", "tester"));
        when(authProvider.doGetAuthInfo()).thenReturn(authInfo);
        Assert.assertEquals(true, authExpressionRoot.hasRole("admin"));
        Assert.assertEquals(true, authExpressionRoot.hasRole("admin", "xxx"));
        Assert.assertEquals(false, authExpressionRoot.hasRole("xxx1"));
        Assert.assertEquals(false, authExpressionRoot.hasRole("xxx1", "xxx2"));

        authInfo.setAuthExpired(true);
        Assert.assertEquals(false, authExpressionRoot.hasRole("admin"));
    }

    @Test
    public void testHasAllRole() throws Exception {
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuthExpired(false);
        authInfo.setRoles(Arrays.asList("admin", "developer", "tester"));
        when(authProvider.doGetAuthInfo()).thenReturn(authInfo);
        Assert.assertEquals(true, authExpressionRoot.hasAllRole("admin", "developer"));
        Assert.assertEquals(false, authExpressionRoot.hasAllRole("admin", "xxx"));

        authInfo.setAuthExpired(true);
        Assert.assertEquals(false, authExpressionRoot.hasAllRole("admin", "developer"));
    }

    @Test
    public void testHasPermission() throws Exception {
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuthExpired(false);
        authInfo.setPermissions(Arrays.asList("article:read", "article:delete", "article:add"));
        when(authProvider.doGetAuthInfo()).thenReturn(authInfo);
        Assert.assertEquals(true, authExpressionRoot.hasPermission("article:read", "article:delete"));
        Assert.assertEquals(true, authExpressionRoot.hasPermission("article:read", "xxx"));
        Assert.assertEquals(false, authExpressionRoot.hasPermission("xxx1"));
        Assert.assertEquals(false, authExpressionRoot.hasPermission("xxx1", "xxx2"));

        authInfo.setAuthExpired(true);
        Assert.assertEquals(false, authExpressionRoot.hasRole("article:read"));
    }

    @Test
    public void testHasAllPermission() throws Exception {
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuthExpired(false);
        authInfo.setPermissions(Arrays.asList("article:read", "article:delete", "article:add"));
        when(authProvider.doGetAuthInfo()).thenReturn(authInfo);
        Assert.assertEquals(true, authExpressionRoot.hasAllPermission("article:read", "article:delete"));
        Assert.assertEquals(false, authExpressionRoot.hasAllPermission("article:read", "xxx"));

        authInfo.setAuthExpired(true);
        Assert.assertEquals(false, authExpressionRoot.hasAllPermission("article:read", "article:delete"));
    }
}

