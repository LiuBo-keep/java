package com.hp.java.zhujie;

/**
 * 注解的使用：
 * 当注解中定义了属性时，在使用时必须给属性赋值;
 * 当注解中定义了默认属性时，在使用时我们可以不用赋值使用默认，也可以赋值覆盖默认值；
 * 当注解中定了了属性名称为value的属性时，如果只给属性名为value的属性赋值时，可以不写属性名称直接赋值.(@MyAnno3(20)==@MyAnno3(valus=20))
 */
@MyAnno1(age = 10,name = "张三")
@MyAnno2(age = 50)
@MyAnno3(20)
public class ReaterZhuJie2 {
}

//定义注解1
@interface MyAnno1{
    /**
     * 定义属性：
     * 格式： 类型  名称 ()
     * @return
     */
    int age ();
    String name ();
}
//定义注解2
@interface MyAnno2{
    /**
     * 在定义属性时，可以指定默认值
     * 格式：类型 名称（）default 默认值
     */
    int age () default 100;
}
//定义注解3
@interface MyAnno3{
    /**
     * 在定义属性时，如果属性名称为value时，在使用时就有特权
     */
    int value();
    String name () default "zhangsan";
}
