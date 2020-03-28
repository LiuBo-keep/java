##1.什么是注解
     语法：@注解名称
             注解的作用：代替xml配置文件
                 servlet3.0中，就可以不在使用web.xml文件，而是所有配置都使用注解
             注解是由框架来读取使用的
             
## 2.注解的使用
             *定义注解类  ：框架的工作
             *使用注解：我们的工作
             *读取注解(反射)：框架的工作
 
## 3.定义注解
            *所以的注解都是Annotation的子类
            *@interface A{}    

## 使用注解
          注解的作用目标
            *类
            *方法
            *构造器
            *参数
            *局部变量
            *包 
          
## 注解的属性
*定义属性：
                >格式：类型 属性名 ()；
                >@interface MyAnno1{
                          int age ();
                          String name ();
                      }

         *使用注解时给属性赋值
                >@MyAnno1(age = 10,name = "zhangsan")

         *注解属性的默认值:在定义注解时，可以给注解指定默认值！ 
               > int age () default 100;
               >在使用注解时，可以不给带有默认值的属性赋值，如果赋值就把默认属性值给替换了

         *名为value的属性的特权
               >当使用注解时，如果只给名为value的属性赋值时，可以省略“value=”，列如：@MyAnno1(value="hello"),可以书写成@MyAnno1("hello")！

         *注解属性的类型
               >8种基本类型
               >String
               >Enum
               >Class
               >注解类型
               >以上类型的一维数组类型 （当给数组类型的属性赋值时，若数组元素的个数为1时，可以省略大括号）
```
@interface MyAnno4{
    int a ();
    String b ();
    MyEnum c();
    Class d ();
    String [] e();
    MyAnno5 f ();
}

@interface MyAnno5{
    int i ();
}

enum MyEnum{
    A,B,C
}

@MyAnno4(
        a=10,
        b="",
        c=MyEnum.A,
        d=String.class,
        e={""},
        f= @MyAnno5 (
                i=10
        )
)   //在这个类上使用注解
public class b {
}
```
        
## 注解的作用目标限定以及保存策略限定
     *让一个注解，他的作用目标只能在类上，不能在方法上，这就叫作用目标的限定
             >在定义注解时，给注解添加注解，这个注解是@Target
  ````
  @Target(value = {ElementType.METHOD})
  @interface MyAnno6{
      int a ();
  }
  ````                                           
     *保留策略
                >源代码文件(SOURCE)：注解只能在源代码中存在，当编译时被忽略了
                >字节码文件(CLASS)：注解在源代码中存在，然后编译时会把注解信息放到了class文件，但JVM在加载类时，会忽略注解
                >JVM中(RUNTIME)：注解在源代码，字节码文件中存在，并且在JVM加载时，会把注解加载到JVM内存中（他是唯一可反射注解！）
  ````
  @Retention(RetentionPolicy.RUNTIME)
  @interface MyAnno6{
      int a ();
  }

  ````                       