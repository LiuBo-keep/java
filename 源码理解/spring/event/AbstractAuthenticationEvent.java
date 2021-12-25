package org.springframework.security.authentication.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

// 抽象认证事件
public abstract class AbstractAuthenticationEvent extends ApplicationEvent {
	// 构造器 初始化 认证发送
    public AbstractAuthenticationEvent(Authentication authentication) {
        super(authentication);
    }
	
	// 获取事件源
    public Authentication getAuthentication() {
        return (Authentication)super.getSource();
    }
}
