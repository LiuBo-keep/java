package com.hp.java.proxy.demo2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class IvoProxy implements InvocationHandler {
    private Waiter waiter=null;

    public IvoProxy(Waiter waiter) {
        this.waiter = waiter;
    }

    //增强内容
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("微笑..");
        this.waiter.serve();
        System.out.println("再见..");
        return null;
    }
}
