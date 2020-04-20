###Reflection(反射)：是Java被视为动态语言的关键，反射机制允许程序在执行期借用Reflection API取得任何类的内部信息，并能直接操作任意对象的内部属性及方法。

###加载完类之后，在堆内存的方法区中就会产生一个Class类型的对象（
一个类只有一个Class对象）,这个对象就包含了完整的类的结构信息。我们
通过对这个对象看到类的结构。这个对象就像一面镜子，透过这个镜子
看到类的结构，所以，我们形象的称为：反射。

##### Class本身也是一个类
##### Class对象只能由系统创建对象
##### 一个加载的类在JVM中只会有一个Class实例
##### 一个Class对象对应的是一个加载到JVM中的一个.class文件
##### 每个类的实例都会记得自己是由哪个Class实例所生成
##### 通过Class可以完整的得到一个类中的所有被加载的结构

###Class类常用的方法
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200420124850130.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

###获取Class类的实例
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200420125118845.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)


##### 哪些类型可以有Class对象
1.class：外部类，成员(成员内部类，静态内部类)，局部内部类，匿名内部类

2.interface：接口


3.[]：数组

4.enum：枚举

5.annotation：注解@interface

6.primitive type：基本数据类型

7.void
