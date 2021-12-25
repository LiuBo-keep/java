package org.springframework.context;

import java.time.Clock;
import java.util.EventObject;

// 应用事件
public abstract class ApplicationEvent extends EventObject {
    private static final long serialVersionUID = 7099057708183571937L;
	
	// 事件发送时间
    private final long timestamp;
	
	// 构造器 初始化 数据源
    public ApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }
	
	// 构造器 初始化 数据源 时间
    public ApplicationEvent(Object source, Clock clock) {
        super(source);
        this.timestamp = clock.millis();
    }

    public final long getTimestamp() {
        return this.timestamp;
    }
}