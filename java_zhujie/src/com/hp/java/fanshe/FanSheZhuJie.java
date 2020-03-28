package com.hp.java.fanshe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 *
 * 反射注解
 *            *要求：注解的保留策略必须时RUNTIME
 *
 *           *反射注解需要从作用目标上返回：
 *              >类上的注解，需要使用Class来获取
 *              >方法上的注解，需要使用Method来获取
 *              >构造方法上的注解，需要使用Construcator来获取
 *              >成员上的注解，需要使用Filed来获取
 *
 *          *Class
 *          *Method，Construcator，Filed他们的共同父类AccessibleObject
 *
 *          *他们都有一个方法：
 *                      >Annotation getAnnotation(class),返回目标上指定类型的注解
 *                      >Annotation [] getAnnotation(),返回目标上的所有注解
 */
public class FanSheZhuJie {
    public static void main(String[] args) throws NoSuchMethodException {
        //1.得到作用目标:类
        Class<D> c=D.class;
        //2.获取指定类型的注解
        MyAnn myAnn=c.getAnnotation(MyAnn.class);
        //输出属性
        System.out.println(myAnn.name()+","+myAnn.age()+","+myAnn.sex());

        //1.得到作用目标:方法
        Class<D> c1=D.class;
        Method method=c1.getMethod("fun1");
        //2.获取指定类型的注解
        MyAnn myAnn1=method.getAnnotation(MyAnn.class);
        //输出属性
        System.out.println(myAnn1.name()+","+myAnn1.age()+","+myAnn1.sex());

    }
}

@MyAnn(name = "zhangsan",age = 12,sex = "nan")
class D{
    @MyAnn(name = "lisi",age = 15,sex = "nan")
    public void fun1(){

    }
}

@Target(value = {ElementType.METHOD,ElementType.TYPE})//定义作用于
@Retention(RetentionPolicy.RUNTIME)//定义保留策略
@interface MyAnn{
    String name();
    int age();
    String sex();
}