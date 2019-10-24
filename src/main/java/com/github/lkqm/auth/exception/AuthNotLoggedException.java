package com.github.lkqm.auth.exception;

/**
 * 未登录异常
 */
public class AuthNotLoggedException extends AuthException {

    public AuthNotLoggedException(String message) {
        super(message);
    }
}
