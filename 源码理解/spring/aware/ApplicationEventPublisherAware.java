package org.springframework.context;

import org.springframework.beans.factory.Aware;

// 获取事件广播器，发布事件使用
public interface ApplicationEventPublisherAware extends Aware {
    void setApplicationEventPublisher(ApplicationEventPublisher var1);
}