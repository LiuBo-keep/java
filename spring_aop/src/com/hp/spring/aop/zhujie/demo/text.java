package com.hp.spring.aop.zhujie.demo;

import com.hp.spring.aop.xml.bean.User;
import com.hp.spring.aop.zhujie.bean.Testcher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//测试
public class text {
    public static void main(String[] args) {
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("com/hp/spring/aop/zhujie/application.xml");
        Testcher teacher=(Testcher) applicationContext.getBean("teacher");
        teacher.add();
        System.out.println("----------------------");
        teacher.delete();
        System.out.println("----------------------");
        teacher.updata();
    }
}
