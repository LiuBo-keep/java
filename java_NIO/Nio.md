
# NIO

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200725234817971.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)



## 向缓冲区添加数据，以及切换模式即读取数据(从写到读需要切换模式)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200725231949460.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

```java
/**
 * @ClassName NioTest
 * @Description TODO
 * @Author 17126
 * @Date 2020/7/25 22:47
 *
 *
 * 1.缓冲区(Buffer)：在Java NIO 中负责数据的存储。
 * 缓冲区就是数组，用于存储不同数据类型的数据
 *
 * 根据数据类型不同(boolean 除外)，提供了相应类型的缓冲区
 *
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloutBuffer
 * DoubleBuffer
 *
 *
 * 上述缓冲区的管理方式几乎一致，通过allocate() 获取缓冲区
 *
 *
 * 2.缓冲区存取数据的两个核心方法
 * put()：存入数据到缓冲区
 * get()：获取缓冲区中的数据
 *
 *
 * 3.缓冲区中的四个核心属性
 *     //标记，表示记录当前position的位置，可以通过reset()恢复到mark的位置
 *     private int mark = -1;
 *     //位置,表示缓冲区中正在操作数据的位置
 *     private int position = 0;
 *     //界限，表示缓冲区中可以操作数据的大小(limit后数据不能进行读写)
 *     private int limit;
 *     //容量，表示缓冲区最大存储数据的容量，一旦声明不能改变
 *     private int capacity;
 *
 *     position<limit<capacity
 */
public class NioTest {

    @Test
    public void test1(){
        //定义数据
        String str="abcde";

        //获取缓冲区并指定缓冲区大小
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        System.out.println("----------------allocate----------------------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //向缓冲区添加数据
        byteBuffer.put(str.getBytes());

        System.out.println("----------------put----------------------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //读取数据需要切换模式
        byteBuffer.flip();

        System.out.println("----------------flip----------------------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        System.out.println("----------------get----------------------");
        //读取缓冲区中的数据
        byte [] aByte = new byte[byteBuffer.limit()];
        byteBuffer.get(aByte);

        System.out.println(new String(aByte));

        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //rewind():可以重复读取数据
        System.out.println("----------------rewind----------------------");
        byteBuffer.rewind();
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());


        //clear()：清空缓冲区,但是缓冲区中的数据依然存在，但是处于“被遗忘”状态
        System.out.println("----------------clear----------------------");
        byteBuffer.clear();
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
    }
}

```


## mark方法

```java

 public class demo{
     @Test
          public void test2(){
              //定义数据
              String str="abcde";
      
              ByteBuffer allocate = ByteBuffer.allocate(1024);
      
              allocate.put(str.getBytes());
      
              allocate.flip();
      
              byte[] bytes = new byte[2];
      
              allocate.get(bytes, 0, 2);
              System.out.println(new String(bytes));
              System.out.println(allocate.position());
      
              //标记：当前position的位置
              allocate.mark();
      
              allocate.get(bytes, 0, 2);
              System.out.println(new String(bytes));
              System.out.println(allocate.position());
      
              //reset()；恢复到mark的位置
              allocate.reset();
              System.out.println(allocate.position());
          }
          
         // 结果：
         // ab
         // 2
         // cd
         // 4
         // 2
 }
```


## 直接缓冲区与非直接缓冲区
非直接缓冲区：通过allocate()方法分别缓冲区，将缓冲区建立在JVM的内存中

直接缓冲区：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在物理内存中，可以提供效率

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200725235651108.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200726001041587.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200726001706649.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

```java

/**
 * @ClassName NioTest2
 * @Description TODO
 * @Author 17126
 * @Date 2020/7/25 23:53
 *
 *
 * 1.直接缓冲区与非直接缓冲区
 * 非直接缓冲区：通过allocate()方法分别缓冲区，将缓冲区建立在JVM的内存中
 * 直接缓冲区：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在物理内存中，可以提供效率
 */
public class NioTest2 {

    @Test
    public void fun(){

        //创建直接缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

        //判断是否是直接缓冲区
        System.out.println(byteBuffer.isDirect());
    }
}
```


## 通道
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200726002858872.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200726003007744.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200726004143196.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)


## 分散与聚集

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200726215429410.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200726215607561.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)


## 网络传输阻塞与非阻塞
- 阻塞(通道，依赖通道传输数据)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200726235711532.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
当客户端向服务端进行写数据时，服务端会派一个线程去接受数据，由于客户端的数据非常多，传输的
非常慢，所以服务端的线程会一直等待客户端线程的数据传输，在此期间服务端线程不能做别的事情，
直到客户端将数据传输完，服务端的线程才开始工作，这样会降低CPU的使用率。

-非阻塞(通道加缓冲区，依赖缓冲区传输数据)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200727000130879.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
当客户端向服务端进行写数据时，服务端会派一个线程去接受数据，但是在NIO模式下会存在一个选择器，
在客户端与服务端之间，当客户端向服务端发送数据时，先会创建一个通道注册到选择器上，再提高
缓冲区向选择器发送数据，选择器会监控这个通道上发送的缓冲区数据，在没有加载完毕时，服务端的线程
可以去做任何的事情不用等待，直到选择器发现客户端将数据发送载完毕时，将所有的数据放入一个缓冲区，
选择器创建一个通道连接服务端，才会通知服务器线程来接收数据，一次接收完毕，不用等待，这样大大
提高了CPU的使用率。


## 管道(Pipe)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200727002022116.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
