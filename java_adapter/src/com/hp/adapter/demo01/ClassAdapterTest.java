package com.hp.adapter.demo01;

/**
 * @author LiuBo
 * @date 2021/8/30
 * @Description TODO  客户端代码
 */
public class ClassAdapterTest {
    public static void main(String[] args) {
        System.out.println("类适配器模式测试：");
        Target target = new ClassAdapter();
        target.request();
    }
}
