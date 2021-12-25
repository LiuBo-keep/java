package org.springframework.context;

// 应用事件发布接口
@FunctionalInterface
public interface ApplicationEventPublisher {
	// 发布事件
    default void publishEvent(ApplicationEvent event) {
        this.publishEvent((Object)event);
    }
	
	// 发布事件
    void publishEvent(Object var1);
}