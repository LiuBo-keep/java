package com.hp.java.proxy.demo2;

public class CglibProxy extends ManWaiter{
    @Override
    public void serve() {
        System.out.println("微笑``");
        super.serve();
        System.out.println("再见..");
    }
}
