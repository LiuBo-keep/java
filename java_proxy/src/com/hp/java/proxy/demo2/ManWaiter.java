package com.hp.java.proxy.demo2;

public class ManWaiter implements Waiter{
    @Override
    public void serve() {
        System.out.println("服务中...");
    }
}
