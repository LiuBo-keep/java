package stream.charDemo;

import org.junit.Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @ClassName Demo1
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/15 14:55
 */
public class Demo1 {

    //一次读取一个字符
    @Test
    public void fun1()throws IOException {
        FileReader fileReader = new FileReader("a.txt");
       int len =0;
      while ((len=fileReader.read())!=-1){
          System.out.print((char)len);
      }

      fileReader.close();
    }

    //一次读取多个字符
    @Test
    public void fun2()throws IOException{
        FileReader fileReader = new FileReader("a.txt");

        char[] chars = new char[1024];
        int len = 0;

        while ((len=fileReader.read(chars))!=-1){
            System.out.println(new String(chars,0,len));
        }

        fileReader.close();
    }


    //一次写一个字符
    @Test
    public void fun3()throws IOException{
        FileWriter fileWriter = new FileWriter("b.txt");
        fileWriter.write(97);

    }
}
