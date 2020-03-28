package com.hp.java.zhujie;

/**
 * 使用不同属性的注解
 * 当给数组赋值时，如果只有一个值时，可以省略大括号(f={"a"}==f="a")
 */
@MyAnn1(
    a=1,
    b="hello",
    c=MyEnum.A,
    d=String.class,
    e=@MyAnn2(),
    f={"",""}
)
public class ReaterZhuJie3 {
}

/**
 * 定义注解，
 * 使用不同类型的属性
 */
@interface MyAnn1{
    //整形
    int a();
    //字符串
    String b();
    //枚举
    MyEnum c();
    //Class
    Class d();
    //注解
    MyAnn2 e();
    //数组
    String [] f();

}

@interface MyAnn2{

}

//定义枚举类
enum MyEnum{
    A,B,C
}
