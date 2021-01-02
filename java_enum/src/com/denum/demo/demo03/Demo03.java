package com.denum.demo.demo03;

import com.denum.demo.demo04.Season;

/**
 * @ClassName Demo03
 * @Description TODO valueOf和values方法
 * @Author liubo
 * @Date 2021/1/2 17:48
 */
public class Demo03 {

    public static void main(String[] args) {
        // 获取当前枚举类的所有对象
        Season[] values = Season.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println(values[i]);
        }

        System.out.println("---------------");

        // 获取指定对象信息
        Season spning = Season.valueOf("SPNING");
        System.out.printf(spning.toString());
    }
}
