package com.hp.adapter.demo02;


/**
 * @author LiuBo
 * @date 2021/8/30
 * @Description 客户端代码
 */
public class ObjectAdapterTest {
    public static void main(String[] args)
    {
        System.out.println("对象适配器模式测试：");
        Adaptee adaptee = new Adaptee();
        Target target = new ObjectAdapter(adaptee);
        target.request();
    }
}
