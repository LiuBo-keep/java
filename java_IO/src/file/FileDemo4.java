package file;

import org.junit.Test;

import java.io.File;

/**
 * @ClassName FileDemo4
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/11 16:52
 */
public class FileDemo4 {
    /*
     File类中的常用的获取的方法：
     1.返回此File(抽象路径名)的绝对路径名字符串。
      public String getAbsolutePath()
     2.将此File转换为路径名称字符串
     public String getPath()
     3.返回由此File表示的文件或文件夹名称
     public String getName()
     4.返回由此File表示的文件的长度
     public long length()
    */

    //返回此File(抽象路径名)的绝对路径名字符串。
    //      public String getAbsolutePath()
    @Test
    public void fun1(){
        File file=new File("F:\\复习在\\-\\java_IO\\a.txt");
        String absolutePath1 = file.getAbsolutePath();
        System.out.println(absolutePath1); //F:\复习在\-\java_IO\a.txt

        File file1=new File("a.txt");
        String absolutePath2 = file1.getAbsolutePath();
        System.out.println(absolutePath2); //F:\复习在\-\java_IO\a.txt
    }


    //将此File转换为路径名称字符串
    //     public String getPath()
    @Test
    public void fun2(){
        File file1=new File("F:\\复习在\\-\\java_IO\\a.txt");
        File file2=new File("a.txt");
        String absolutePath1 = file1.getPath();
        System.out.println(absolutePath1); //F:\复习在\-\java_IO\a.txt
        String absolutePath2 = file2.getPath();
        System.out.println(absolutePath2); //a.txt
    }

    // 返回由此File表示的文件或文件夹名称(获取构造方法传递路径的结尾部分；文件/文件夹)
    //     public String getName()
    @Test
    public void fun3(){
        File file1=new File("F:\\复习在\\-\\java_IO\\a.txt");
        String name = file1.getName();
        System.out.println(name);
    }

    //返回由此File表示的文件的长度(获取的是构造方法指定的文件的大小，以字节为单位)
    //     public long length()
    //注意：文件夹是没有大小概念的，不能获取文件夹的大小
    //如果构造方法中给出的路径不存在，则返回0
    @Test
    public void fun4(){
        //获取文件大小
        File file1=new File("F:\\复习在\\-\\java_IO\\src\\file\\FileDemo1.java");
        System.out.println(file1.length());//1570
        //获取不存在的文件大小
        File file2=new File("F:\\复习在\\-\\java_IO\\src\\file\\FileDemo1.txt");
        System.out.println(file2.length());//0
        //获取文件夹大小
        File file3=new File("F:\\复习在\\-\\java_IO\\src\\file");
        System.out.println(file3.length());//0
    }
}
