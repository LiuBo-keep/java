package com.hp.java.proxy.demo1;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Proxy1 {

    @Test
    public void fun(){
        /**
         *三大参数：
         * 1.ClassLoader
         * 方法需要动态生成一个类，这个类实现了A,B接口，然后创建这个类的对象
         * 需要生成一个类，这份类也需要加载到方法区中，有类加载器加载
         * 2.Class [] interface
         * 他是要实现的接口们
         * 3.InvocationHandler（接口，内部只有一个方法：invoke(Object proxy,Method method,Object[] args)throws Throwable）
         * 它是调用处理器
         * 4.注意：代理对象的实现的所有接口中的方法，内容都是调用InvocationHandler中的invoke方法，
         */
        ClassLoader classLoader=this.getClass().getClassLoader();
        InvocationHandler h=(p,m,a)->{
            System.out.println("hello proxy");
            return null;
        };
        //创建代理对象
        Object o=Proxy.newProxyInstance(classLoader,new Class[]{A.class,B.class},h);

        A a=(A)o;
        B b=(B)o;

        a.a();
        b.b();
    }
}

interface A{
    void a();
}

interface B{
    void b();
}
