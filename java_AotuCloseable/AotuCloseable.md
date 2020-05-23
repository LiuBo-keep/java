### 用处

AutoCloseable主要用于日后进行资源开发的处理上，以实现资源的自动关闭(资源释放)；
列如：在以后的进行文件，网络数据库开发中由于服务器的资源有限，所以使用之后一定要关闭
资源，这样才可以被更多的使用者所使用。


### 案例：
通过一个消息的发送处理来完成：

方式1：手工实现资源处理（demo1）

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200523183045323.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

方式2：自动实现资源处理（demo2） 

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020052318321084.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

既然所有的资源完成处理之后都必须进行关闭操作，那么能否实现自动关闭资源，在这个要求下：
推出了AutoCloseable接口。
    
    public interface AutoCloseable
    JDK1.7出现
    方法：
    public void close()throws Exception；
    
注意：要想实现自动关闭处理，除了要使用AutoCloseable之外，还需要结合异常处理语句才能正常调用。


## 扩展 Java中try()...catch()用法

     在stackoverflow偶尔看到的一个关于try()...catch()的用法，通常我们使用try...catch()捕获异常的，如
     果遇到类似IO流的处理，要在finally部分关闭IO流，当然这个是JDK1.7之前的写法了；在JDK7优化后的
     try-with-resource语句，该语句确保了每个资源,在语句结束时关闭。所谓的资源是指在程序完成后，必须关闭的
     流对象。写在()里面的流对象对应的类都实现了自动关闭接口AutoCloseable；
     
     
     格式：
     try (创建流对象语句，如果多个,使用';'隔开) {
         // 读写数据
     } catch (IOException e) {
         e.printStackTrace();
     }
     
     演示代码，下面的test目录是不存在的，运行会抛出异常；
     JDK1.7之前
     FileWriter fw = null;
      
     try {
         fw = new FileWriter("test\\test.txt");
         fw.write("test");
     } catch (Exception ex) {
         ex.printStackTrace();
     } finally {
         if (fw != null) {
             try {
                 fw.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }
     
     JDK1.7之后
     try(FileWriter fw = new FileWriter("test.txt")) {
         fw.write("test");
     } catch(Exception ex) {
         ex.printStackTrace();
     }
     
       
    

