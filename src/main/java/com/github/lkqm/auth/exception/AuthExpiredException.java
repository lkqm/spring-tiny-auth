package com.github.lkqm.auth.exception;

/**
 * 会话过期异常
 */
public class AuthExpiredException extends AuthException {

    public AuthExpiredException(String message) {
        super(message);
    }
}
