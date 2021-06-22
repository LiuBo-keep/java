package com.example.demo.listener;

import com.example.demo.bean.User;
import com.example.demo.event.SmsEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author LiuBo
 * @date 2021/6/22
 * @Description 事件监听
 */

@Component
public class EventListener implements ApplicationListener<SmsEvent> {
    @Override
    public void onApplicationEvent(SmsEvent smsEvent) {
        User user = smsEvent.getUser();
        System.out.println("给" + user.getName() + "发短信");
    }
}
