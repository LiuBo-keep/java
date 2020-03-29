package com.hp.java.proxy.demo2;

import org.junit.Test;

import java.lang.reflect.Proxy;

/**
 *
 * 在服务的实现类型方法前后添加微笑和再见
 * 目标对象：ManWaiter
 * 增强方式：
 *     1.cglib
 *     2.静态
 *     3.动态
 */
public class WaiterDemo {
    //cglib动态代理
    @Test
    public void fun1(){
        CglibProxy proxy=new CglibProxy();
        proxy.serve();
    }

    //使用静态代理增强
    @Test
    public void fun2(){
        ManWaiter manWaiter=new ManWaiter();//目标对象
        JingTaiProxy proxy=new JingTaiProxy(manWaiter);
        proxy.serve();
    }

    //使用动态代理增强
    @Test
    public void fun3(){
        ManWaiter manWaiter=new ManWaiter();//目标对象
        //得到代理对象，代理对象就是在目标对象的基础上进行了增强的对象
        Object obj=Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{Waiter.class},new IvoProxy(manWaiter) );
        Waiter waiter=(Waiter)obj;
        waiter.serve();
    }
}

