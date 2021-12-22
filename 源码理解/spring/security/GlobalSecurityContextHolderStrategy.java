package org.springframework.security.core.context;

import org.springframework.util.Assert;

// 全局安全上下文持有策略
final class GlobalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
	
	// 安全上下文
    private static SecurityContext contextHolder;

    GlobalSecurityContextHolderStrategy() {
    }
	
	// 清除安全上下文
    public void clearContext() {
        contextHolder = null;
    }
	
	// 获取安全上下文
    public SecurityContext getContext() {
        if (contextHolder == null) {
            contextHolder = new SecurityContextImpl();
        }

        return contextHolder;
    }
	
	// 设置安全上下文
    public void setContext(SecurityContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder = context;
    }

    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }
}
