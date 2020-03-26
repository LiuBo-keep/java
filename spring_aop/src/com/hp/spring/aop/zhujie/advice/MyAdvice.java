package com.hp.spring.aop.zhujie.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
//通知类

@Aspect
public class MyAdvice {

    @Before(value = "execution(* com.hp.spring.aop.zhujie.bean.Testcher.add(..))")
    public void before(){
        System.out.println("这是前置通知");
    }

    @After(value = "execution(* com.hp.spring.aop.zhujie.bean.Testcher.delete(..))")
    public void after(){
        System.out.println("这是后置通知");
    }

    @Around(value = "execution(* com.hp.spring.aop.zhujie.bean.Testcher.updata(..))")
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
