package com.hp.spring.aop.zhujie.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//目标类
@Component(value = "teacher")
public class Testcher {
    public void add(){
        System.out.println("add...");
    }

    public void delete(){
        System.out.println("delete...");
    }

    public void updata(){
        System.out.println("update...");
    }
}
