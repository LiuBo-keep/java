package com.hp.adapter.demo02;

/**
 * @author LiuBo
 * @date 2021/8/30
 * @Description TODO 对象适配器类
 */
public class ObjectAdapter implements Target {

    private Adaptee adaptee;

    public ObjectAdapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void request() {
        adaptee.specificRequest();
    }
}
