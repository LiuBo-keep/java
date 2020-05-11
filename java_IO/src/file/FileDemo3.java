package file;

import org.junit.Test;

import java.io.File;

/**
 * @ClassName FileDemo3
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/11 16:38
 */

public class FileDemo3 {
    /*
     File类的构造方法：
     1.从父抽象路径名和子路径名字符串创建新的 File实例。
     File(File parent, String child)

     2.通过将给定的路径名字符串转换为抽象路径名来创建新的 File实例。
     File(String pathname)

     3.从父路径名字符串和子路径名字符串创建新的 File实例。
     File(String parent, String child)

     4.通过将给定的 file: URI转换为抽象路径名来创建新的 File实例。
     File(URI uri)
    */


    //通过将给定的路径名字符串转换为抽象路径名来创建新的 File实例。
    //     File(String pathname)
    @Test
    public void fun1(){
        File file=new File("a.txt");
        System.out.println(file);
    }

    //从父路径名字符串和子路径名字符串创建新的 File实例。
    //     File(String parent, String child)
    @Test
    public void fun2(){
      File file=new File("a","b.text");
      System.out.println(file);
    }

    //从父抽象路径名和子路径名字符串创建新的 File实例。
    //     File(File parent, String child)
    @Test
    public void fun3(){
      File file1=new File("c:\\");
      File file=new File(file1,"c");
      System.out.println(file);
    }

}
