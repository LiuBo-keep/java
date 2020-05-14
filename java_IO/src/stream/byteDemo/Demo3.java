package stream.byteDemo;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ClassName Demo3
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/14 19:27
 */
public class Demo3 {
    /*
      字节输入流的超类(抽象类)：InPutStream
      基本功能：
      1.关闭此输出流并释放与此流相关的任何系统资源
      public void close();
      2.从输入流中读取一些字节，并将他们存储到字节数组b中
      public int read(byte[] b);
      3.从输入流读取数据的下一个字节
      public abstract int read();

      子类：FileInPutStream继承的InPutStream 文件字节输出流(是将数据写入File中的流)

      构造方法：
      1.public FileInputStream(File file);
      2.public FileInputStream(String name)

      构造方法的作用：
      1.创建FileInputStream对象
      2.会把FileInputStream对象指定构造器中要读取的文件

      注意：要读取的文件一定要操作不然会报错

    */

    // 从输入流读取数据的下一个字节
    //      public abstract int read();
    @Test
    public void fun1() throws IOException {
        FileInputStream inputStream = new FileInputStream("a.txt");
        //使用一次读取一个字节的方式读取a.txt文件
        //注意：读取到文件的末尾返回-1，表示已经读取完毕
        /*int read = inputStream.read();
        System.out.println(read);
        read = inputStream.read();
        System.out.println(read);
        read = inputStream.read();
        System.out.println(read);
        read = inputStream.read();
        System.out.println(read);*/

        //发现会用重复操作，这时可以采用循环解决
        //由于不知道循环次数可以采用while循环，结束条件为不能为-1
        int len = 0;
        while ((len=inputStream.read())!=-1){
            System.out.println(len);
            //将字节转换为字符输出
            System.out.println((char) len);
        }
        inputStream.close();
    }

    // 从输入流中读取一些字节，并将他们存储到字节数组b中
    //      public int read(byte[] b);
    @Test
    public void fun2()throws IOException{
        FileInputStream inputStream = new FileInputStream("a.txt");

        //存储读取到底字节，1024表示一次可以读取到的字节数
        byte[] bytes = new byte[1024];
        //存储每次读取到文件中字节的个数，即每次读取的有效个数
        int len =0;
        //判断每次读取的字节个数不等-1表示还有数据，等于-1表示没有数据
        while ((len=inputStream.read(bytes))!=-1){
            System.out.println(new String(bytes,0,len));
        }
        inputStream.close();
    }

    //案例将a文件中的数据读取到b文件中(将a中的内容复制到b)
    @Test
    public void fun4()throws IOException{
        //输入
        FileInputStream fileInputStream = new FileInputStream("a.txt");
        //输出
        FileOutputStream fileOutputStream = new FileOutputStream("b.txt");

        //是读取多个字节方式
        byte[] bytes = new byte[1024];
        //记录有效数
        int len = 0;

        while ((len=fileInputStream.read(bytes))!=-1){
            fileOutputStream.write(bytes,0,len);
        }
    }

    //案例复制图片
    @Test
    public void fun5()throws IOException{
        FileInputStream inputStream = new FileInputStream("F:\\个人\\图片\\5.jpg");
        FileOutputStream outputStream = new FileOutputStream("c.jpg");

        //是读取多个字节方式
        byte[] bytes = new byte[1024];
        //记录有效数
        int len = 0;

        while ((len=inputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,len);
        }
    }
}
