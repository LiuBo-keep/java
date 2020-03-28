package com.hp.java.zhujie;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ReaterZhuJie5 {
}

/**
 * 注解保留策略
 *        >源代码文件(SOURCE)：注解只能在源代码中存在，当编译时被忽略了
 *        >字节码文件(CLASS)：注解在源代码中存在，然后编译时会把注解信息放到了class文件，但JVM在加载类时，会忽略注解
 *        >JVM中(RUNTIME)：注解在源代码，字节码文件中存在，并且在JVM加载时，会把注解加载到JVM内存中（他是唯一可反射注解！）
 *
 *   使用@Retention(RetentionPolicy.RUNTIME)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@interface An{

}
