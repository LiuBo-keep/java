package com.denum.demo.demo03;

/**
 * @ClassName Season
 * @Description TODO
 * @Author 17126
 * @Date 2021/1/2 17:00
 */

/**
 * 枚举类默认继承Enum类
 */
public enum Season {
    /**
     * 提供当前枚举类对象，多个对象之前用逗号隔开，末尾的对象用分号结束
     */
    SPNING("春天", "春暖花开"),
    SUMMER("夏天", "烈日炎炎"),
    AUTUMN("秋天", "秋高气爽"),
    WINTER("冬天", "寒风刺骨");

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
