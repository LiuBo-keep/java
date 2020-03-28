package com.hp.java.zhujie;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

public class ReateZhuJie4 {
}

/**
 *  *让一个注解，他的作用目标只能在类上，不能在方法上，这就叫作用目标的限定
 *              >在定义注解时，给注解添加注解，这个注解是@Target
 *
 *              public @interface Target {
 *     /**
 *      * Returns an array of the kinds of elements an annotation type
 *      * can be applied to.
 *      * @return an array of the kinds of elements an annotation type
 *      * can be applied to
 *
 *       ElementType []  value();//是一个枚举数组
 *      }
 *
 *      public enum ElementType {
 *         //Class, interface (including annotation type), or enum declaration
 *         TYPE,
 *         // Field declaration (includes enum constants)
 *         FIELD,
 *         //Method declaration
 *         METHOD,
 *         //Formal parameter declaration
 *         PARAMETER,
 *         //Constructor declaration
 *         CONSTRUCTOR,
 *         //Local variable declaration
 *         LOCAL_VARIABLE,
 *         。。。。
 *      }
 *
 */
@Target(value ={ElementType.FIELD})
@interface Ann{

}
