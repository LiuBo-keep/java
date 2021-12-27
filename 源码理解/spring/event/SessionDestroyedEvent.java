package org.springframework.security.core.session;

import java.util.List;
import org.springframework.security.core.context.SecurityContext;

// 会话销毁事件
public abstract class SessionDestroyedEvent extends AbstractSessionEvent {
	// 构造器 初始化 数据源
    public SessionDestroyedEvent(Object source) {
        super(source);
    }
	// 获取SecurityContext 上下文
    public abstract List<SecurityContext> getSecurityContexts();
	// sessionid
    public abstract String getId();
}