package com.hp.java.fanshe;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 读取泛型：
 *    反射泛型信息：
 *              >Class---->Type getGenericSuperclass()返回表示此Class所表示的实体的直接超类(父)的Type
 *              >Type----->ParameterizedType,把Type强转成ParameterizedType类型。
 *              >ParameterizedType--->参数化类型=A<String>
 *              >ParameterizedType:Type[] getActualTypeArguments(),得到A<String>中的String
 *              >Type[] 就是Class[] ，我们就得到了类型参数
 */
public class FanSheFanXing {

    public static void main(String[] args) {
        new C();
    }
}

class A<T>{
    /*
     *在这里获取子类传递的泛型信息，要得到一个Class
     */
    public A(){
        Class aClass=this.getClass();//获取子类的class
        Type type=aClass.getGenericSuperclass();
        ParameterizedType pType=(ParameterizedType)type;
        Type[] type1=pType.getActualTypeArguments();
        Class c=(Class) type1[0];
        System.out.println(c);
    }

}

class B extends A<String>{

}

class C extends A<Integer>{

}