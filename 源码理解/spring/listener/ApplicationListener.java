package org.springframework.context;

import java.util.EventListener;
import java.util.function.Consumer;

@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
	// 在发送事件时 监听器要做什么事
    void onApplicationEvent(E var1);

    static <T> ApplicationListener<PayloadApplicationEvent<T>> forPayload(Consumer<T> consumer) {
        return (event) -> {
            consumer.accept(event.getPayload());
        };
    }
}