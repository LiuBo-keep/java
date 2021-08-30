package com.hp.spring.aop.xml.demo;

import com.hp.spring.aop.xml.bean.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



//测试
public class text {

    public static void main(String[] args){
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("com/hp/spring/aop/xml/application.xml");
        User user=(User) applicationContext.getBean("user");
        user.add();
        System.out.println("----------------------");
        user.delete();
        System.out.println("----------------------");
        user.updata();

    }
}

