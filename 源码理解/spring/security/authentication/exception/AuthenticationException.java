package org.springframework.security.core;
// 认证异常
public abstract class AuthenticationException extends RuntimeException {
    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthenticationException(String msg) {
        super(msg);
    }
}