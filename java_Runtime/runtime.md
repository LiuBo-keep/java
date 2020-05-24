### Runtime类

---

#### **简介** 
Runtime描述的是运行时的状态，也就是说在整个的JVM之中，Runtime类
是唯一一个与JVM运行状态有关的类并且默认提供有一个该类的实例化对象。



---


     出现时间：JDK1.0
     由于在每一个JVM进程里面允许提供有一个Runtime类的对象，所以这个类的
     构造方法默认被私有化了，那么该类是单类设计模式。
     
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200524134354486.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
 
   
---

由于Runtime类属于单例模式，如果要想获取实例化对象，那么就可以依靠类中
的getRuntime方法，
    
    1.获取实例化对象
    publci static Runtime getRuntime();    
    2.获取本机的CPU内核数
    public int availableProcessors();
    3.获取最大可用内存空间(默认是本机的4/1)
    publci long maxMemory();
    4.获取可用内存空间(默认是本机的64/1)
    public long totalMemory();
    5.获取空闲的内存空间
    public long freeMemory();
    6.手动进行GC处理
    public void gc();