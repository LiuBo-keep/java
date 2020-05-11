package file;

import org.junit.Test;

import java.io.File;

/**
 * @ClassName FileDemo7
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/11 19:05
 */
public class FileDemo7 {

    /*
     File类的文件夹(目录)遍历
     1.返回一个String数组，表示该File文件夹(目录)中的所有子文件或文件夹(目录)
     public String[] list();
     2.返回一个File数组，表示该File文件夹(目录)中的所有子文件或文件夹(目录)
     public File[] listFiles();

     注意：list方法和listFile方法遍历的是构造方法中给出的目录
          如果构造方法中给出的目录的路径不存在，会抛出空指针异常
          如果构造方法中给出的路径不是一个目录，会抛出空指针异常

          所以在使用list或listFile方法时先去判断文件夹(目录)是否存在或是判断是否是一个文件夹(目录)
    */


    //返回一个String数组，表示该File文件夹(目录)中的所有子文件或文件夹(目录)
    //     public String[] list();
    @Test
    public void fun1(){
        File file = new File("F:\\复习在\\-\\java_IO\\11");
        //判断路径是否真实存在
        if (file.exists()){
            //判断是否是文件夹
            if (file.isDirectory()){
                String[] list = file.list();
                for (String str:list
                        ) {
                    System.out.println(str);
                }
            }
        }
    }


    //返回一个File数组，表示该File文件夹(目录)中的所有子文件或文件夹(目录)
    //     public File[] listFiles();
    //注意：调用listFile方法的File对象，表示的必须是实际存在的文件夹，否则返回null，无法进行遍历
    @Test
    public void fun2(){
        File file = new File("F:\\复习在\\-\\java_IO\\11");
        //判断路径是否真实存在
        if (file.exists()){
            //判断是否是文件夹
           if (file.isDirectory()){
               File[] files = file.listFiles();
               for (File str:files
                       ) {
                   System.out.println(str);
               }
           }
        }
    }
}
