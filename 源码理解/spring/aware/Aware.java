package org.springframework.beans.factory;

// Spring提供了广泛的Aware回调接口，让bean向容器表明它们需要某种基础设施依赖
// 通常Aware有这样一个规则：Aware接口的名称，表示依赖对象的类名称。
// 例如，一个bean需要使用ApplicationContext，实现ApplicationContextAware接口即可。
//    MyApplicationContext 想在类中使用ApplicationContext 则只需要实现ApplicationContextAware接口即可
//    表示向spring容器表明自己需要ApplicationContext依赖 容器会在初始化时发现MyApplicationContext实现了ApplicationContextAware接口
//    则会将ApplicationContext实现注入到MyApplicationContext的applicationContext属性上
//    class MyApplicationContext implements ApplicationContextAware {
//     private ApplicationContext applicationContext;	
//	
//   }
//  url: https://blog.csdn.net/Bronze5/article/details/105902892
public interface Aware {
}