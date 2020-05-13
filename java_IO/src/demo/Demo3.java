package demo;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @ClassName Demo3
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/13 17:19
 */
public class Demo3 {
    //创建File对象法方式
    @Test
    public void fun1(){
        File file = new File("a.txt");
        new File("c:\\","a");
        new File(new File("a"),"a");
        new File("");
    }

    //File类中常用的获取方法
    @Test
    public void fun2(){
        File file = new File("a");
        //获取文件绝对路径
        String path = file.getAbsolutePath();
        //获取文件的相对路径
        String path1 = file.getPath();
        //获取文件名称
        String name = file.getName();
        //获取文件的大小(以字节为单位)
        long length = file.length();
    }

    //File类常用的判断方法
    @Test
    public void fun3(){
        File file = new File("aa", "aa");
        //判断路径是否真实存在
        boolean exists = file.exists();
        //判断是否是文件夹
        boolean directory = file.isDirectory();
        //判断是否是文件
        boolean file1 = file.isFile();
    }
    
    //File类常用的操作文件/文件夹的方式
    @Test
    public void fun4() throws IOException {
        File file = new File(new File(""), "");
        //创建文件
        boolean newFile = file.createNewFile();
        //创建单级文件
        boolean mkdir = file.mkdir();
        //创建多级文件
        boolean mkdirs = file.mkdirs();
        //删除文件或文件夹
        boolean delete = file.delete();
    }
    
    //File获取当前文件夹下的文件或文件夹
    @Test
    public void fun5(){
        File file = new File("");
        //以字符串的形式返回
        String[] list = file.list();
        //以File对象的形式返回
        File[] files = file.listFiles();
    }

    //案例递归打印文件
    @Test
    public void fun6(){
        File file=new File("11");
        show(file);
    }

    private void show(File file) {
        //打印当前文件名称
        System.out.println(file.getName());

        //进入方法先进行判断
        if (!file.exists()&&null==file){
            return;
        }

        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f:files
                 ) {
                //如果是文件夹则继续
                if (f.isDirectory()){
                    show(f);
                }else {//不是文件夹则打印文件
                    System.out.println(f.getName());
                }
            }
        }
    }

    //创建多级目录
    @Test
    public void fun7(){
        File file = new File("11\\22\\33");
        if (!file.exists()){
            file.mkdirs();
        }
    }
}
