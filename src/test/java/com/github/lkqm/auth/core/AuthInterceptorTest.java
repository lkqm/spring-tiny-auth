package com.github.lkqm.auth.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class AuthInterceptorTest {
    @Mock
    TinyAuth tinyAuth;
    @InjectMocks
    AuthInterceptor authInterceptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void preHandle() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HandlerMethod handler = mock(HandlerMethod.class);
        when(handler.getBeanType()).thenReturn((Class) String.class);

        when(request.getMethod()).thenReturn("OPTIONS");
        authInterceptor.preHandle(request, response, handler);
        verify(tinyAuth, never()).auth(request, handler);

        when(request.getMethod()).thenReturn("GET");
        authInterceptor.preHandle(request, response, handler);
        verify(tinyAuth).auth(request, handler);
    }
}

