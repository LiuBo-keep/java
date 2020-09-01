package Nio.demo1;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * @ClassName Buffer2
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/29 0:02
 */
public class Buffer2 {

    public static void main(String[] args) throws Exception {
        FileInputStream inputStream = new FileInputStream("a.txt");
        FileChannel channel = inputStream.getChannel();

        ByteBuffer[] buffers = new ByteBuffer[2];
        buffers[0] = ByteBuffer.allocate(2);
        buffers[1] = ByteBuffer.allocate(3);

        channel.read(buffers);

        Arrays.stream(buffers).forEach(byteBuffer -> byteBuffer.flip());

        for (int i = 0; i <buffers.length ; i++) {
            System.out.println(new String(buffers[i].array()));
        }
    }
}
