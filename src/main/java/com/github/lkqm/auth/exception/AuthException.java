package com.github.lkqm.auth.exception;

/**
 * 权限认证基类异常
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
