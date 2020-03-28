package com.hp.java.zhujie;

/**
 * 注解的使用
 */
@MyAnnol
public class ReaterZhuJie {

    @MyAnnol
    private String name;

    @MyAnnol
    public ReaterZhuJie() {
    }

    @MyAnnol
    public void add(){

    }

    public void setName(@MyAnnol String name) {
        @MyAnnol
        String username=name;
    }
}

/**
 * 定义注解
 */
@interface MyAnnol{

        }