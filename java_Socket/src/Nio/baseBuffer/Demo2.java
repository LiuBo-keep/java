package Nio.baseBuffer;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName Demo2
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/20 23:20
 */
public class Demo2 {

    public static void main(String[] args) throws Exception{
        String str = "好好学习Java";

        ByteBuffer allocate = ByteBuffer.allocate(1024);

        allocate.put(str.getBytes());

        FileOutputStream fileOutputStream = new FileOutputStream("a.txt");

        FileChannel channel = fileOutputStream.getChannel();

        allocate.flip();

        channel.write(allocate);

        fileOutputStream.close();
    }
}
