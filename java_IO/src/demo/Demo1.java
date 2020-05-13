package demo;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @ClassName Demo1
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/12 18:22
 */
public class Demo1 {


    public static void main(String[] args) throws IOException {
       //fun1();
       //fun2();
       //fun3();
       //fun4();
       //fun5();
    }

    //创建单级文件
    @Test
    public static void fun1(){
        System.out.println("请输入您想创建的文件名称:");
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();

        if (null!=next){
            File file = new File(next);
            file.mkdir();
        }
    }

    //创建多级目录
    @Test
    public static void fun2(){
        System.out.println("请输入您想创建的文件名称");
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();

        if (null!=next){
            File file = new File(next);
            file.mkdirs();
        }
    }

    //创建文件
    @Test
    public static void fun3() throws IOException {
        System.out.println("请输入您想创建的文件名称");
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();

        if (null!=next){
            File file = new File(next);
            file.createNewFile();
        }
    }

    //查看指定路径文件大小
    @Test
    public static void fun4(){
        System.out.println("请输入您想查看的文件名称");
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();

        if (null!=next){
            File file = new File(next);
            if (file.exists()){
                long length = file.length();
                System.out.println(length+"字节");
            }
        }
    }

    //删除指定文件
    @Test
    public static void fun5(){
        System.out.println("请输入您想删除的文件名称");
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();

        if (null!=next){
            File file = new File(next);
            if (file.exists()){
                if (file.isDirectory()){
                    file.delete();
                }
            }
        }
    }
}
