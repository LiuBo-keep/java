package com.hp.spring.aop.xml.advice;

import org.aspectj.lang.ProceedingJoinPoint;

//通知类
public class MyAdvice {
    public void before(){
        System.out.println("这是前置通知");
    }

    public void after(){
        System.out.println("这是后置通知");
    }

    public void rount(ProceedingJoinPoint point){
        System.out.println("环绕之前");
        try {
            point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.println("环绕之后");
    }
}
