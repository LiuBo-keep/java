package com.denum.demo.demo01;

/**
 * @ClassName Season
 * @Description TODO
 * @Author liubo
 * @Date 2021/1/2 16:52
 */

/**
 * 定义季节枚举类
 */
public class Season {
    /**
     * 声明属性
     */
    private final String name;
    private final String desc;

    /**
     * 私有类的构造器
     */
    private Season(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    /**
     * 当前枚举类的多个对象
     */
    public static final Season SPNING = new Season("春天", "春暖花开");
    public static final Season SUMMER = new Season("夏天", "烈日炎炎");
    public static final Season AUTUMN = new Season("秋天", "秋高气爽");
    public static final Season WINTER = new Season("冬天", "寒风刺骨");

    /**
     * 每个枚举类的属性方法
     */
    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * toString方法
     */
    @Override
    public String toString() {
        return "Season{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
