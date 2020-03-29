package com.hp.java.proxy.demo3;

import com.hp.java.proxy.demo2.Waiter;

public class ManWaiter implements Waiter{
    @Override
    public void serve() {
        System.out.println("服务中...");
    }
}
