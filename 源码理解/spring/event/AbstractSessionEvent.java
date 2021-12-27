package org.springframework.security.core.session;

import org.springframework.context.ApplicationEvent;

// 抽象会话事件
public class AbstractSessionEvent extends ApplicationEvent {
    public AbstractSessionEvent(Object source) {
        super(source);
    }
}