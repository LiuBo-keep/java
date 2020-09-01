package Nio.demo1;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName Channel
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/27 23:01
 */
public class Channel {
    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("a.txt");

        FileChannel channel = fileInputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        channel.read(byteBuffer);

        byteBuffer.flip();

        System.out.println(new String(byteBuffer.array()));

        fileInputStream.close();
    }
}
