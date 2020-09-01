package Nio.baseBuffer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName Demo4
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/20 23:37
 */
public class Demo4 {
    public static void main(String[] args) throws Exception{
        FileInputStream fileInputStream = new FileInputStream("a.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("b.txt");

        FileChannel channel = fileInputStream.getChannel();
        FileChannel channel1 = fileOutputStream.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        channel.read(buffer);

        buffer.flip();

        channel1.write(buffer);

        fileInputStream.close();
        fileOutputStream.close();

    }
}
