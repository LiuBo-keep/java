package org.springframework.security.authentication;

import org.springframework.security.core.AuthenticationException;

// 错误凭据异常
public class BadCredentialsException extends AuthenticationException {
    public BadCredentialsException(String msg) {
        super(msg);
    }

    public BadCredentialsException(String msg, Throwable cause) {
        super(msg, cause);
    }
}