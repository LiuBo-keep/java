package stream.byteDemo;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ClassName Demo2
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/14 17:23
 */

public class Demo2 {
    /*
   数据追加续写
   构造方法：
   1.创建文件输出流以写入由指定的File对象表示的文件
   public FileOutputStream(File file,boolean append);
   2.创建文件输出流以写入由指定名称的文件
   public FileOutputStream(String name,boolean append);

   注意：这两个构造方法中传入法boolean参数值，要么为true(表示追加数据)，要么为false(清空原有数据)

   写换行：
   windows:\r\n
   Linux:\n
 */

    @Test
    public void fun1() throws IOException {
        FileOutputStream outputStream = new FileOutputStream("a.txt",true);

        outputStream.write("你好".getBytes());

        outputStream.close();
    }

    @Test
    public void fun2() throws IOException {
        FileOutputStream outputStream = new FileOutputStream("a.txt",true);
        //加入换行
        outputStream.write("你好\r\n".getBytes());

        outputStream.close();
    }
}
