package file;

import org.junit.Test;

import java.io.File;

/**
 * @ClassName FileDemo1
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/11 16:13
 */

    /*
     File类:文件和目录路径名的抽象表示。
     File类的方法：
     创建文件夹或文件
     删除文件夹或文件
     获取文件夹或文件
     判断文件夹或文件是否存在
     遍历文件夹或文件
     获取文件夹或文件大小

     重点：记住三个单词
     1.file  文件
     2.directory   文件夹
     3.path  路径
    */
public class FileDemo1 {

    @Test
    public void fun1(){
    /*

       File类中的静态方法：
      static String pathSeparator;与系统相关的路径分隔符字符，为方便起见，表示为字符串。
      static char pathSeparatorChar;与系统相关的路径分隔符。

     static String separator;与系统相关的默认名称 - 分隔符字符，以方便的方式表示为字符串。
     static char separatorChar;与系统相关的默认名称分隔符。
    */

        //路径分割符：windows：分号(;),Linux是冒号(:)
        String path1 = File.pathSeparator;
        System.out.println(path1);

        //文件名称分隔符：windows：反斜杠(\)，Linux正斜杠(/)
        //操作路径：路径不能写死
        //C:\develop\a\a.txt
        //"C:"+File.separator+"develop"+File.separator+"a"+File.separator+"a.txt"
        String separator = File.separator;
        System.out.println(separator);
    }
}
