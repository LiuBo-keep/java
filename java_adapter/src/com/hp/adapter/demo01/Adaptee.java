package com.hp.adapter.demo01;

/**
 * @author LiuBo
 * @date 2021/8/30
 * @Description TODO 适配者接口
 */
public class Adaptee {
    public void specificRequest() {
        System.out.println("适配者中的业务代码被调用！");
    }
}
