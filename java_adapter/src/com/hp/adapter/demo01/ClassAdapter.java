package com.hp.adapter.demo01;

/**
 * @author LiuBo
 * @date 2021/8/30
 * @Description TODO 类适配器类
 */
public class ClassAdapter extends Adaptee implements Target {

    @Override
    public void request() {
        specificRequest();
    }
}
