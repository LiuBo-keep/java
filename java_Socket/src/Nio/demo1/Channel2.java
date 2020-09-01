package Nio.demo1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @ClassName Channel2
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/27 23:33
 */
public class Channel2 {
    public static void main(String[] args)throws Exception {
        FileInputStream fileInputStream = new FileInputStream("1.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream("3.jpg");
        FileChannel channel1 = fileInputStream.getChannel();
        FileChannel channel2 = fileOutputStream.getChannel();

        channel2.transferFrom(channel1,0,channel1.size());

        fileOutputStream.close();
        fileInputStream.close();
    }
}
