package com.example.demo.event;

import com.example.demo.bean.User;
import org.springframework.context.ApplicationEvent;

/**
 * @author LiuBo
 * @date 2021/6/22
 * @Description 描述
 */
public class SmsEvent extends ApplicationEvent {
    private User user;

    public SmsEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
