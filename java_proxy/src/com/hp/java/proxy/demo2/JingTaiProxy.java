package com.hp.java.proxy.demo2;

public class JingTaiProxy implements Waiter{

    private Waiter w=null;

    public JingTaiProxy(Waiter w) {
        this.w = w;
    }

    @Override
    public void serve() {
        System.out.println("微笑``");
        w.serve();;
        System.out.println("再见..");
    }
}
