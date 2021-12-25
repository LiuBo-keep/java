package org.springframework.security.authentication.event;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

// 抽象认证失败事件
public abstract class AbstractAuthenticationFailureEvent extends AbstractAuthenticationEvent {
	// 认证失败异常
    private final AuthenticationException exception;
	
	// 构造器 初始化 认证实现 认证失败异常
    public AbstractAuthenticationFailureEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication);
        Assert.notNull(exception, "AuthenticationException is required");
        this.exception = exception;
    }
	
	// 获取异常信息
    public AuthenticationException getException() {
        return this.exception;
    }
}