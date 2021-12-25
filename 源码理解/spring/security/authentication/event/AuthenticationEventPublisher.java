package org.springframework.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

// 认证事件发布者 用户授权成功或失败的通知机制
public interface AuthenticationEventPublisher {
	
	// 成功时调用
    void publishAuthenticationSuccess(Authentication var1);
	
	// 失败时调用
    void publishAuthenticationFailure(AuthenticationException var1, Authentication var2);
}