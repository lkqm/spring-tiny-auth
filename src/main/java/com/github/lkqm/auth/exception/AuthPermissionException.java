package com.github.lkqm.auth.exception;

/**
 * 无权限访问异常
 */
public class AuthPermissionException extends AuthException {
    public AuthPermissionException(String message) {
        super(message);
    }
}
