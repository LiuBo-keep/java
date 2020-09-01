package Nio.baseBuffer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @ClassName Demo5
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/20 23:56
 */
public class Demo5 {
    public static void main(String[] args) throws Exception{
        FileInputStream fileInputStream = new FileInputStream("1.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream("2.jpg");

        FileChannel channel1 = fileInputStream.getChannel();
        FileChannel channel2 = fileOutputStream.getChannel();

        channel2.transferFrom(channel1,0,channel1.size());

        fileOutputStream.close();
        fileInputStream.close();

    }
}
