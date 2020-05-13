package stream.byteDemo;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ClassName Demo1
 * @Description 字节流(万能流，因为一切都是字节)
 * @Author 17126
 * @Date 2020/5/13 18:24
 */

public class Demo1 {
    /*
      字节输出流的超类(抽象类)：OutPutStream
      基本功能：
      1.关闭此输出流并释放与此流相关的任何系统资源
      public void close();
      2.刷新此输出流并强制任何缓冲的输出字节被写入
      public void flush();
      3.将b.length字节从指定的字节数组写入此输出流
      public void write(byte[] b);
      4.从指定的字节数组写入len字节，从偏移量off开始输出到此输出流
      public void write(byte[] b,int off,int len);
      5.将指定的字节输出流
      public abstract void write(int b);

      子类：FileOutPutStream继承的OutPutStream 文件字节输出流(是将数据写入File中的流)

      构造方法：
      1.创建一个向具有指定名称的文件中写入数据的输出文件流
      public FileOutPutStream(String name);
      2.创建一个向指定File对象表示的文件中写入数据的文件输出流
      public FileOutPutStream(File file);

      构造方法的作用：
      1.创建一个FileOutPutStream对象
      2.会根据构造方法中传递的文件/文件路径，创建一个空的文件(判断文件是否存在，存在则指向文件，反之创建一个空的文件)
      3.会把FileOutPutStream对象指向创建好的文件

      字节输出流的使用步骤：
      1.创建一个FileOutPutStream对象，构造方法中传递写入数据的目的地
      2.调用FileOutPutStream对象中的write方法把数据写入到文件中
      3.释放资源(流的使用会占用一定的内存，使用完毕要把内存清空，提高程序效率)
    */

    //构造方法
    @Test
    public void fun1() throws FileNotFoundException {
        File file = new File("a.txt");
        FileOutputStream fileOutputStream1 = new FileOutputStream(file);

        FileOutputStream fileOutputStream2 = new FileOutputStream("b.txt");
    }

    //向文件中写入一个字节
    @Test
    public void fun2() throws IOException {
        //构造器会判断a.txt文件是否存在，如果存在直接操作，如果不存在先创建a.txt，再操作
        FileOutputStream fileOutputStream = new FileOutputStream("a.txt");
        //向目标文件中写入一个字符
        fileOutputStream.write(97);
        //释放资源
        fileOutputStream.close();
    }
}
