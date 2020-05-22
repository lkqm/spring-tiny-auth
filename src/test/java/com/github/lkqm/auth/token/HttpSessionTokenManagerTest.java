package com.github.lkqm.auth.token;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestContextHolder.class, ServletRequestAttributes.class})
public class HttpSessionTokenManagerTest {

    HttpSession httpSession;
    HttpSessionTokenManager tokenManager;
    String attributeName = "__TOKEN__";

    @Before
    public void init() throws Exception {
        this.httpSession = mock(HttpSession.class);
        this.tokenManager = new HttpSessionTokenManager(attributeName);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(httpSession);

        ServletRequestAttributes requestAttributes = mock(ServletRequestAttributes.class);
        when(requestAttributes.getRequest()).thenReturn(request);

        PowerMockito.mockStatic(RequestContextHolder.class);
        when(RequestContextHolder.getRequestAttributes()).thenReturn(requestAttributes);
    }

    @Test
    public void generateToken() {
        tokenManager.generateToken("data");
        verify(httpSession).setAttribute(eq(attributeName), any());
    }

    @Test
    public void removeToken() {
        tokenManager.removeToken(null);
        verify(httpSession).removeAttribute(eq(attributeName));
    }

    @Test
    public void getTokenData() {
        when(httpSession.getAttribute(attributeName)).thenReturn("520");
        Integer value = tokenManager.getTokenData(null, Integer.class);
        assertEquals(Integer.valueOf(520), value);
        verify(httpSession).getAttribute(eq(attributeName));
    }
}