package file;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @ClassName FileDemo6
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/11 17:38
 */
public class FileDemo6 {
    /*
    File类的创建删除的方法
    1.当且仅当具有该名称的文件尚不存在时，创建一个新的空文件(创建文件)
    public boolean createNewFile();
    2.删除由此File表示的文件或文件夹(删除文件或文件夹)
    public boolean delete()
    3.创建由此File表示的文件夹(创建单级目录)
    public boolean mkdir()
    4.创建由此File表示的文件夹，包括任何必须但不存在的父文件夹(创建多级目录)
    public boolean mkdirs()
    */

    //1.当且仅当具有该名称的文件尚不存在时，创建一个新的空文件
    //    public boolean createNewFile();
    @Test
    public void fun1() throws IOException {
        File file = new File("F://复习在//-//java_IO//a.txt");
        //判断文件是否存在
        boolean flag = file.isFile();
        if (!flag){
            boolean f=file.createNewFile();
            if (f){
                System.out.println("创建成功");
            }
        }else {
            System.out.println("文件已经存在");
        }

    }

    //创建由此File表示的文件夹(创建单级目录)
    //    public boolean mkdir()
    @Test
    public void fun2(){
        File file = new File("F://复习在//-//java_IO//text");
        //判断文件夹是否存
        boolean directory = file.isDirectory();
        if (!directory){
            boolean mkdir = file.mkdir();
            if (mkdir){
                System.out.println("文件夹创建成功");
            }
        }else {
            System.out.println("文件夹已经存在");
        }
    }

    //创建由此File表示的文件夹，包括任何必须但不存在的父文件夹(创建多级目录)
    //    public boolean mkdirs()
    @Test
    public void fun3(){
        File file = new File("F://复习在//-//java_IO//11//22//33");
        if (!file.isDirectory()){
            boolean mkdirs = file.mkdirs();
            if (mkdirs){
                System.out.println("多级文件夹创建成功");
            }
        }else {
            System.out.println("文件夹已经存在");
        }
    }

    //删除由此File表示的文件或文件夹(删除文件或文件夹)
    //    public boolean delete()
    @Test
    public void fun4(){
        File file1 = new File("F://复习在//-//java_IO//a.txt");
        File file2 = new File("F://复习在//-//java_IO//text");
        File file3 = new File("F://复习在//-//java_IO//11");

        //判断是否存在
        if (file1.exists()||file2.exists()||file3.exists()){
            if (file1.isFile()){
                file1.delete();
                System.out.println("文件删除成功");
            }if (file2.isDirectory()){
                file2.delete();
                System.out.println("文件夹删除成功");
            }if (file3.isDirectory()){
                file3.delete();
                //删除多级文件夹只能手动改变构造器参数，一级一级删除
                System.out.println("多级文件夹删除成功");
            }
        }
    }

    //自动删除多级目录(文件夹)
    @Test
    public void fun5(){
        File file = new File("A://复习在//-//java_IO//aaa.txt");

        file.list();
        //deleteFile(file);
    }

    private void deleteFile(File file) {
        file.list();

    }

    @Test
    public void fun6(){
        int i =10;
        int sum=sum(i);
        System.out.println(sum);

    }

    private int sum(int i) {
        if (i==1){
            return 1;
        }

        return i+sum(i-1);
    }
}
