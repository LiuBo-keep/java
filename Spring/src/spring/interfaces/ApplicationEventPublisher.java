package spring.interfaces;

import spring.abstracts.ApplicationEvent;

/**
 * @Description 发布事件
 * @Author 17126
 * @Date 2021/6/22 14:10
 */
public interface ApplicationEventPublisher {

    /**
     * 发布事件
     */
    default void publishEvent(ApplicationEvent event) {
        this.publishEvent((Object) event);
    }

    /**
     * 发布事件
     */
    void publishEvent(Object var1);
}
