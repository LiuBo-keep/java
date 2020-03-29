package com.hp.java.proxy.demo3;

public class ProxyDemo {
    public static void main(String[] args) {
        ProxyFactory factory=new ProxyFactory();
        factory.setTargetObject(new ManWaiter());
        factory.setBeforeAdvice(()->{
            System.out.println("微笑..");
        });
        factory.setAfterAdvice(()->{
            System.out.println("再见..");
        });

        Waiter waiter= (Waiter) factory.createProxy();
        waiter.serve();
    }
}
