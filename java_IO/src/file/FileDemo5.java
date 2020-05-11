package file;

import org.junit.Test;

import java.io.File;

/**
 * @ClassName FileDemo5
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/11 17:18
 */
public class FileDemo5 {
    /*
     File类的判断功能：
     1.此File表示的文件或文件夹是否实际存在
     public boolean exists();
     2.此File表示的是否为文件夹
     public boolean isDirectory();
     3.此File表示的是否为文件
     public boolean isFile();
    */

    //此File表示的文件或文件夹是否实际存在
    //     public boolean exists();
    @Test
    public void fun1(){
        File file1=new File("F:\\复习在\\-\\java_IO\\src\\file\\FileDemo2.java");
        System.out.println(file1.exists());
        File file2=new File("src");
        System.out.println(file2.exists());
    }


    //此File表示的是否为文件夹
    //     public boolean isDirectory();
    @Test
    public void fun2(){
        //文件
        File file1=new File("F:\\复习在\\-\\java_IO\\src\\file\\FileDemo2.java");
        System.out.println(file1.isDirectory());//false
        //文件夹
        File file2=new File("src");
        System.out.println(file2.isDirectory());//true
    }


    //此File表示的是否为文件
    //     public boolean isFile();
    @Test
    public void fun3(){
        //文件
        File file1=new File("F:\\复习在\\-\\java_IO\\src\\file\\FileDemo2.java");
        System.out.println(file1.isFile());//true
        //文件夹
        File file2=new File("src");
        System.out.println(file2.isFile());//false
    }
}
