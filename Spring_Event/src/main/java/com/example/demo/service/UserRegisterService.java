package com.example.demo.service;

import com.example.demo.bean.User;
import com.example.demo.event.SmsEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

/**
 * @author LiuBo
 * @date 2021/6/22
 * @Description 描述
 */

@Service
public class UserRegisterService implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    public boolean register(User user) {

        //用户注册
        System.out.println("[service]用户[" + user + "]注册成功！");

        //消息发布
        applicationEventPublisher.publishEvent(new SmsEvent(this, user));

        return true;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
